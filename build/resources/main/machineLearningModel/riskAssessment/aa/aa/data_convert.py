import os
import json
import datetime
import re
import csv
import sys
import importlib
importlib.reload(sys)

#os.path.realpath——获取当前执行脚本的绝对路径
script_path = os.path.split(os.path.realpath(__file__))[0]

vital_sign_list1 = ['呼吸', '脉搏', '血压high', '血压Low', '体温', '血氧']

lab_test_list = ['二氧化碳', '无机磷', '氯化物', '葡萄糖', '钙', '钠', '钾', '镁', '尿素', '肌酐',
                 'eGFR', '尿素肌酐', '钙无机磷', '血清尿酸', '脑利钠肽前体', '血红蛋白测定',
                 '肌酸激酶同工酶定量测定', '肌钙蛋白T', '血浆D-二聚体测定', '红细胞比积测定', 'γ-谷氨酰基转移酶',
                 '丙氨酸氨基转移酶', '乳酸脱氢酶', '天冬氨酸氨基转移酶', '总蛋白', '总胆红素', '血清白蛋白',
                 '直接胆红素', '碱性磷酸酶']

medicine_list = ['袢利尿剂', '胺碘酮', 'v2受体拮抗剂', '噻嗪类', '保钾利尿剂', 'ACEI', 'ARB', 'beta-blocker',
                 '醛固酮受体拮抗剂', '强心药', '扩血管药', '抗生素']

# 有关键字匹配吗
operation_list = ['既往血运重建手术']

basic_info = ['性别', '年龄']

vital_sign_list2 = ['体重', '身高']

diagnosis_list = ['心肌病', '瓣膜病', '心房纤颤或心房扑动', '高脂血症', '高血压病',
                  '糖尿病', '睡眠呼吸暂停', '贫血', '感染', '冠心病']


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

        # discharge_time = visit_all_info['visitInfo']['dischargeTime']
        # print(discharge_time)

        vital_signs_info1, vital_signs_info2 = parse_vital_signs(visit_all_info['vitalSigns'])
        lab_tests = parse_labtests(visit_all_info['labTests'])
        medicine = parse_medicines(visit_all_info['medicines'])
        operation = parse_operations(visit_all_info['operations'])
        egfr = calculate_egfr(visit_all_info['labTests'], visit_all_info['basicInfo'],
                              visit_all_info['visitInfo'])
        diagnoses = parse_diagnosis(visit_all_info['diagnoses'])
        general_info = parse_general_info(visit_all_info['basicInfo'], visit_all_info['visitInfo'])

        # 需要填充egfr
        lab_tests[10] = egfr

        res_vector = compose_to_one_vector(vital_signs_info1, lab_tests, medicine, operation, general_info,
                                           vital_signs_info2, diagnoses)
        return res_vector


def parse_vital_signs(vital_signs_info):
    vital_sign_dict = {'呼吸': [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)],
                       '脉搏': [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)],
                       '血压high': [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)],
                       "血压Low": [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)],
                       "体温": [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)],
                       "血氧": [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)],
                       "体重": [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)],
                       "身高": [-1, datetime.datetime(2020, 1, 1, 0, 0, 0, 0)]}

    for item in vital_signs_info:
        record_time = datetime.datetime.strptime(item['recordTime'], '%Y-%m-%d %H:%M:%S.%f')
        vital_sign = item['key']['vitalSign']
        result = item['result']

        if not vital_sign_dict.__contains__(vital_sign):
            continue

        if record_time < vital_sign_dict[vital_sign][1]:
            vital_sign_dict[vital_sign] = [result, record_time]

    breath = vital_sign_dict['呼吸'][0]
    heart_beat = vital_sign_dict['脉搏'][0]
    systolic_blood_pressure = vital_sign_dict['血压high'][0]
    diastolic_blood_pressure = vital_sign_dict['血压Low'][0]
    body_temperature = vital_sign_dict['体温'][0]
    blood_oxygen = vital_sign_dict['血氧'][0]

    height = vital_sign_dict['身高'][0]
    weight = vital_sign_dict['体重'][0]

    vector1 = [breath, heart_beat, systolic_blood_pressure, diastolic_blood_pressure, body_temperature, blood_oxygen]
    vector2 = [weight, height]

    return vector1, vector2


def parse_labtests(labtests_info):
    # 其中,'eGFR', '尿素肌酐', '钙无机磷'另外算,eGFR还没加进去
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

    if lab_test_result['尿素'][0] != -1 and lab_test_result['肌酐'][0] != -1:
        lab_test_result['尿素肌酐'][0] = lab_test_result['尿素'][0] / lab_test_result['肌酐'][0]

    if lab_test_result['钙'][0] != -1 and lab_test_result['无机磷'][0] != -1:
        lab_test_result['钙无机磷'][0] = lab_test_result['钙'][0] / lab_test_result['无机磷'][0]

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
    ##medicines_info就是visit_all_info['visitInfo']
    medicine_list = ['袢利尿剂', '胺碘酮', 'v2受体拮抗剂', '噻嗪类', '保钾利尿剂', 'ACEI', 'ARB', 'βRB',
                     '醛固酮受体拮抗剂', '强心药', '扩血管药', '抗生素']

    drug_map_path = '新AKI药物名称映射.csv'
    drug_map_dict = dict()
    drug_map_set = set()
    with open(os.path.join(script_path, drug_map_path), 'r', encoding='gbk', newline="") as file_:
        csv_reader = csv.reader(file_)
        for line in csv_reader:
            drug_map_set.add(line[0])
            for i in range(len(line)):
                if len(line[i]) >= 2:
                    drug_map_dict[line[i]] = line[0]

    dosage = 0
    f_injection = ['★呋塞米注射液', '速尿注射液', '★注射用呋塞米', '★速尿注射液', '呋塞米注射液', '注射用呋塞米',
                   '速尿', '速尿注射液(输完白蛋白后立即）', '★呋塞米注射', '速尿注射液10mg/H', '★呋塞米注射液（15：00）',
                   '5★呋塞米注射液', '速尿注射', '速尿注射液（2ml/h）', '98速尿注射液', '速尿针', '★呋塞米注射液（输蛋白后30分）',
                   '\\★呋塞米注射液', '呋塞米注射液（输血后30分钟）', '速尿注射液(滴完白旦白1小时后给）', '★呋塞米注射液（白蛋白液末）',
                   '★呋塞米注液', '速尿注射液(输完白蛋白后0.5h)', '注射液呋塞米', '速尿注射液(输液完)']
    f_pill = ['★呋塞米片', '速尿片', '★速尿片', '呋塞米片', '速尿片(双日）', '速尿片(单日)', '速尿片（双日）', '速尿片      （单日）',
              '速尿片(双日)', '速尿片（单日）', '★呋塞米片（周二、周五）', '速尿片       （双日）',
              '速尿片(单)', '速尿片 (单日)', '速尿片（单摆）', '★呋塞米片（双日)', '速尿片(单摆）', '速尿片(下午）', '停速尿片',
              '速尿片(2,5)', '20★速尿片']
    t_injection = ['注射用托拉塞米(南京海辰)', '托拉塞米注射液(南京优科)', '托拉塞米注射液(浙诚意)', '托拉塞米注射液', '特苏尼注射液',
                   '托拉塞米注射液(南京新港)', '注射用托拉塞米', '特苏尼注射液(特批)', '托拉塞米注射液（浙诚意）', '注射用托拉塞米（南京海辰）',
                   '托拉塞米注射液（特批）', '特苏尼注射液(特批）', '托拉塞米注射液(南京海辰)', '托拉塞米注射液（南京优科）',
                   '注射用托拉塞米(南京优科)', '托拉塞米注射液(特批)', '特苏尼注射液（特批）', '托拉塞米（南京优科）', '注射液托拉塞米',
                   '托拉塞米注射液(特批）', '注射用托拉塞米（南京还辰）', '拖拉塞米注射液']
    t_pill = ['托拉塞米片', '托拉塞米胶囊', '托拉塞米', '伊迈格片', '托拉噻米片']
    b_injection = ['★布美他尼注射液', '布美他尼注射液', '利了注射液', '利了', '0布美他尼注射液', '★布美他尼注射', '1布美他尼注射液']
    b_pill = ['布美他尼片', '利了片', '布美他尼胶囊', '布美他尼片（单日）']
    for item in medicines_info:
        if item['orderText'] in f_injection:
            cur_str = item['dosage']
            if len(cur_str) == 0:
                continue
            else:
                cur_dosage = float(cur_str)
                dosage += cur_dosage / 40.0
        elif item['orderText'] in f_pill:
            cur_str = item['dosage']
            if len(cur_str) == 0:
                continue
            else:
                cur_dosage = float(cur_str)
                dosage += cur_dosage / 80.0
        elif item['orderText'] in t_injection or t_pill:
            cur_str = item['dosage']
            if len(cur_str) == 0:
                continue
            else:
                cur_dosage = float(cur_str)
                dosage += cur_dosage / 20.0
        elif item['orderText'] in b_injection or b_pill:
            cur_str = item['dosage']
            if len(cur_str) == 0:
                continue
            else:
                cur_dosage = float(cur_str)
                dosage += cur_dosage / 2.0

    medicines_vector = []
    for item in medicine_list:
        if item == '袢利尿剂':
            medicines_vector.append(dosage)
        else:
            if item in drug_map_set:
                medicines_vector.append(1)
            else:
                medicines_vector.append(0)

    return medicines_vector


# 计算eGfr，排除第一次入院肌酐大于265.2；或者egfr<30
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
        return [0]

    if egfr < 30:
        egfr_vector = 0
    else:
        egfr_vector = scr

    return egfr_vector


def parse_operations(operation_info):
    flag = 0
    for item in operation_info:
        operation = item['operationDesc']
        if len(operation) == 0:
            continue
        for cur in operation_list:
            if cur in operation and '肾动脉支架' not in operation and '肾动脉球囊' not in operation and '下肢动脉球囊' not in operation and '下肢动脉支架' not in operation:
                flag = 1
    return flag


def parse_diagnosis(diagnoses):
    diagnoses_dict = {'心肌病': 0, '瓣膜病': 0, '心房纤颤或心房扑动': 0, '高脂血症': 0, '高血压病': 0,
                      '糖尿病': 0, '睡眠呼吸暂停': 0, '贫血': 0, '感染': 0, '冠心病': 0}
    xinji = ['心肌病', '心肌变性']
    banmo = ['瓣膜性心脏病', '二尖瓣', '三尖瓣', '主动脉瓣', '肺动脉瓣', '心脏瓣膜']
    xinfang = ['特发性心房纤颤|阵发性心房纤颤', '心房纤维性颤动', '心房扑动', '不纯性心房扑动']
    xuezhi = ['高脂血症', '混合性高脂血症', '妊娠合并高脂血症', '高胆固醇血症', '家族性高胆固醇血症']
    # 不是(白大衣性高血压)|(口服避孕药高血压)
    xueya = ['(高血压)']
    # 不是(妊娠糖尿病母亲的婴儿综合征)
    tangniao = ['(糖尿病)']
    shuimian = ['睡眠呼吸暂停']
    pinxue = ['贫血']
    # 不是'非感染性'
    ganran = ['感染']
    guanxin = ['缺血性心', '心肌', '梗', '心绞痛', 'X综合', '急性冠状?动?脉', '冠心病', '冠状动脉粥样硬化性心脏病']

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

        for cur in xinfang:
            if cur in diagnosisDesc:
                diagnoses_dict['心房纤颤或心房扑动'] = 1
                break

        for cur in xuezhi:
            if cur in diagnosisDesc:
                diagnoses_dict['高脂血症'] = 1
                break

        for cur in xueya:
            if cur in diagnosisDesc and '(白大衣性高血压)' not in diagnosisDesc and '(口服避孕药高血压)' not in diagnosisDesc:
                diagnoses_dict['高血压病'] = 1
                break

        for cur in tangniao:
            if cur in diagnosisDesc and '(妊娠糖尿病母亲的婴儿综合征)' not in diagnosisDesc:
                diagnoses_dict['糖尿病'] = 1
                break

        for cur in shuimian:
            if cur in diagnosisDesc:
                diagnoses_dict['睡眠呼吸暂停'] = 1
                break

        for cur in pinxue:
            if cur in diagnosisDesc:
                diagnoses_dict['贫血'] = 1
                break

        for cur in ganran:
            if cur in diagnosisDesc:
                diagnoses_dict['感染'] = 1
                break

        for cur in guanxin:
            if cur in diagnosisDesc:
                diagnoses_dict['冠心病'] = 1
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
def compose_to_one_vector(vital_signs_info1, lab_tests, medicine, operation, basic, vital_signs_info2, diagnoses):
    composed_all_vector = list()
    composed_vector = list()

    for item in vital_signs_info1:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)

    for item in lab_tests:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)

    for item in medicine:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)

    if operation == -1:
        composed_vector.append(0)
    else:
        composed_vector.append(operation)

    for item in basic:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)

    for item in vital_signs_info2:
        if item == -1:
            composed_vector.append(0)
        else:
            composed_vector.append(item)

    for item in diagnoses:
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
    print(res_vector)





