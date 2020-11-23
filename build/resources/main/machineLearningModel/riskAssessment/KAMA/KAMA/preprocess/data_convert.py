import os
import json
import datetime
import re
import csv
import sys
import importlib
importlib.reload(sys)

# 正确结果
#os.path.realpath——获取当前执行脚本的绝对路径
# script_path = os.path.dirname(os.path.realpath('__file__'))
# print(script_path)

script_path = 'G:\server\\aki_system\src\main\\resources\machineLearningModel\\riskAssessment\KAMA\KAMA\preprocess'

vital_sign_list = ['血压Low', '血压high']

lab_test_list = ['eGFR', '甘油三酯', '丙氨酸氨基转移酶', '钾', '总胆红素',
       '血红蛋白测定', '脑利钠肽前体', '尿素', '天冬氨酸氨基转移酶', '低密度脂蛋白胆固醇']

medicine_list = ['抗血小板', '抗凝药物', 'beta受体拮抗剂','正性肌力药物', '血管扩张剂', 'ACEI/ARB', '钙通道阻滞剂']

operation_list = [ '造影', 'PCI']

basic_info = ['性别', '年龄']

diagnosis_list = ['AHF急性心力衰竭', '糖尿病', '瓣膜病', '心肌病', '冠心病', '脑卒中']


def json_parse(json_str):
    content = json.loads(json_str)
    key_list = list(content.keys())
    key_list.sort()

    # 只要第一次的结果
    # first_visit_time_str = content[key_list[0]]['visitInfo']['admissionTime']
    # first_visit_time = datetime.datetime.strptime(first_visit_time_str, '%Y-%m-%d %H:%M:%S.%f')

    res_vector = []
    if len(key_list) > 0:
        visit_all_info = content[key_list[0]]

        vital_signs_info = parse_vital_signs(visit_all_info['vitalSigns'])
        lab_tests = parse_labtests(visit_all_info['labTests'])
        medicine = parse_medicines(visit_all_info['medicines'])
        operation = parse_operations(visit_all_info['operations'])
        egfr = calculate_egfr(visit_all_info['labTests'], visit_all_info['basicInfo'],
                              visit_all_info['visitInfo'])
        diagnoses = parse_diagnosis(visit_all_info['diagnoses'])
        general_info = parse_general_info(visit_all_info['basicInfo'], visit_all_info['visitInfo'])

#         需要填充egfr
        lab_tests[0] = egfr

        res_vector = compose_to_one_vector(vital_signs_info, lab_tests, medicine, operation, general_info,
                                           diagnoses)

    return res_vector


def parse_vital_signs(vital_signs_info):
    vital_sign_dict = {"血压Low": [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)],
    				  '血压high': [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)]}

    for item in vital_signs_info:
        record_time = datetime.datetime.strptime(item['recordTime'], '%Y-%m-%d %H:%M:%S.%f')
        vital_sign = item['key']['vitalSign']
        result = item['result']

        if not vital_sign_dict.__contains__(vital_sign):
            continue

        if record_time < vital_sign_dict[vital_sign][1]:
            vital_sign_dict[vital_sign] = [result, record_time]

    systolic_blood_pressure = vital_sign_dict['血压high'][0]
    diastolic_blood_pressure = vital_sign_dict['血压Low'][0]

    vector1 = [systolic_blood_pressure, diastolic_blood_pressure]

    return vector1


def calculate_egfr(labtest, basic_info, visit_info):
    sex = 1 if basic_info['sex'] == "男" else 0
    current_visit_time_str = visit_info['admissionTime']
    current_visit_time = datetime.datetime.strptime(current_visit_time_str, '%Y-%m-%d %H:%M:%S.%f')
    birthday_str = basic_info['birthday']
    birthday = datetime.datetime.strptime(birthday_str, '%Y-%m-%d %H:%M:%S.%f')
    age = (current_visit_time - birthday).days / 365

    #     basic_vector = []
    #     basic_vector.append(sex)
    #     basic_vector.append(age)

    scr = -1
    for item in labtest:
        if item['labTestItemName'] == '肌酐':
            result = item['result']
            result_list = re.findall('[-+]?[\d]+(?:,\d\d\d)*[.]?\d*(?:[eE][-+]?\d+)?', result)
            if len(result_list) > 0 and float(result_list[0]) > 265.2:
                return 0
            if len(result_list) > 0:
                result = result_list[0]
            if len(result_list) == 0 or len(result) == 0:
                continue
            scr = float(result)
            break

    if sex == 1.0:
        egfr = 186 * ((scr / 88.41) ** -1.154) * (age ** -0.203) * 1
    else:
        egfr = 186 * ((scr / 88.41) ** -1.154) * (age ** -0.203) * 0.742

    if scr == -1:
        return 0

    if egfr < 30:
        egfr_vector = 0
    else:
        egfr_vector = scr

    return egfr_vector


def parse_labtests(labtests_info):
    # 其中,eGFR还没加进去
    required_item_set = set(lab_test_list)

    # initialize
    lab_test_result = dict()
    for key in required_item_set:
        lab_test_result[key] = [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)]

    # read data
    for item in labtests_info:
        lab_test_item_name = item['labTestItemName']
        if not required_item_set.__contains__(lab_test_item_name):
            continue

        result = item['result']
        result_list = re.findall('[-+]?[\d]+(?:,\d\d\d)*[.]?\d*(?:[eE][-+]?\d+)?', result)
        if len(result_list) > 0:
            result = result_list[0]
        if len(result_list) == 0 or len(result) == 0:
            continue
        result = float(result)

        execute_date = datetime.datetime.strptime(item['executeDate'], '%Y-%m-%d %H:%M:%S.%f')

        if execute_date < lab_test_result[lab_test_item_name][1]:
            lab_test_result[lab_test_item_name] = [result, execute_date]

    lab_test_vector = list()
    for item in lab_test_list:
        result, _ = lab_test_result[item]
        # to do,目前把缺失值填0
        if result == -1:
            lab_test_vector.append(0)
        else:
            lab_test_vector.append(result)

    return lab_test_vector


def parse_medicines(medicines_info):
    medicine_list = ['抗血小板', '抗凝药物', 'beta受体拮抗剂','正性肌力药物', '血管扩张剂', 'ACEI/ARB', '钙通道阻滞剂']
    drug_map_path =  'KAMA_Medicine.csv'
    drug_map_dict = dict()
    drug_map_set = set()
    with open(os.path.join(script_path, drug_map_path), 'r', encoding='gbk', newline="") as file_:
        csv_reader = csv.reader(file_)
        for line in csv_reader:
            drug_map_set.add(line[0])
            for i in range(len(line)):
                if len(line[i]) >= 2:
                    drug_map_dict[line[i]] = line[0]

    medicines_vector = []
    for item in medicine_list:
        if item in drug_map_set:
            medicines_vector.append(1)
        else:
            medicines_vector.append(0)

    return medicines_vector


def parse_operations(operation_info):
    operation_vector = [0, 0]
    for item in operation_info:
        operation = item['operationDesc']
        if len(operation) == 0:
            continue
        for cur in operation_list:
            if cur == '造影':
                operation_vector[0] = 1
            if cur == 'PCI':
                operation_vector[1] = 1
    return operation_vector


def parse_diagnosis(diagnoses):
    diagnoses_dict = {'AHF急性心力衰竭': 0, '糖尿病': 0, '瓣膜病': 0, '心肌病': 0, '冠心病': 0, '脑卒中': 0}
    ahf = ['急性心衰', '急性心力衰竭']
    xinji = ['心肌病', '心肌变性']
    banmo = ['瓣膜性心脏病', '二尖瓣', '三尖瓣', '主动脉瓣', '肺动脉瓣', '心脏瓣膜']
    # 不是(妊娠糖尿病母亲的婴儿综合征)
    tangniao = ['(糖尿病)']
    guanxin = ['缺血性心', '心肌', '梗', '心绞痛', 'X综合', '急性冠状?动?脉', '冠心病', '冠状动脉粥样硬化性心脏病']
    cuzhong = ['卒中', '脑梗', '脑血管意外']

    diagnoses_vector = []
    for item in diagnoses:
        diagnosisDesc = item['diagnosisDesc']
        for cur in xinji:
            if cur in diagnosisDesc:
                diagnoses_dict['心肌病'] = 1
                break

        for cur in banmo:
            if cur in diagnosisDesc:
                diagnoses_dict['瓣膜病'] = 1
                break

        for cur in tangniao:
            if cur in diagnosisDesc and '(妊娠糖尿病母亲的婴儿综合征)' not in diagnosisDesc:
                diagnoses_dict['糖尿病'] = 1
                break

        for cur in guanxin:
            if cur in diagnosisDesc:
                diagnoses_dict['冠心病'] = 1
                break

        for cur in ahf:
            if cur in diagnosisDesc:
                diagnoses_dict['AHF急性心力衰竭'] = 1
                break

        for cur in cuzhong:
            if cur in diagnosisDesc:
                diagnoses_dict['卒中'] = 1
                break

    for k, v in diagnoses_dict.items():
        diagnoses_vector.append(v)

    return diagnoses_vector


def parse_general_info(basic_info, visit_info):
    sex = 1 if basic_info['sex'] == "男" else 0
    current_visit_time_str = visit_info['admissionTime']
    current_visit_time = datetime.datetime.strptime(current_visit_time_str, '%Y-%m-%d %H:%M:%S.%f')

    birthday_str = basic_info['birthday']
    birthday = datetime.datetime.strptime(birthday_str, '%Y-%m-%d %H:%M:%S.%f')

    age = (current_visit_time - birthday).days / 365

    general_vector = []
    general_vector.append(sex)
    general_vector.append(age)

    return general_vector


# 合并所有向量
def compose_to_one_vector(vital_signs_info, lab_tests, medicine, operation, basic, diagnoses):
    composed_all_vector = list()
    composed_vector = list()

    for item in diagnoses:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)

    for item in basic:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)

    for item in operation:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)

    for item in medicine:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)

    for item in vital_signs_info:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)

    for item in lab_tests:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)


    composed_all_vector.append(composed_vector)
    # 目前只返回第一条数据
    return composed_all_vector[0]


if __name__ == "__main__":
    argv = sys.argv
    # file_name = argv[1]
    file_name = '1590659091156'

    with open(os.path.join(script_path, file_name), 'r', encoding='utf-8') as file:
        json_str = file.readline()

    res_vector = json_parse(json_str)

    inputs = {"test": res_vector}
    pyTorch_feed_data = json.dumps({'inputs':inputs})
    print(pyTorch_feed_data)