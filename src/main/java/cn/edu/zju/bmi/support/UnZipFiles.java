package cn.edu.zju.bmi.support;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZipFiles
{
    public static void uniZip(String sourceFile, String destination)
    {
        //Open the file
        try(ZipFile file = new ZipFile(sourceFile))
        {
            FileSystem fileSystem = FileSystems.getDefault();
            //Get file entries
            Enumeration<? extends ZipEntry> entries = file.entries();

            //We will unzip files in this folder
            if(!Files.exists(fileSystem.getPath(destination)))
                Files.createDirectory(fileSystem.getPath(destination));

            //Iterate over entries
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();

                // mac系统下自带这一路径，会造成解压出错
                if(entry.getName().contains("__MACOSX"))
                    continue;
                //If directory then create a new directory in uncompressed folder
                if (entry.isDirectory())
                {
                    System.out.println("Creating Directory:" + destination + entry.getName());
                    Files.createDirectories(fileSystem.getPath(destination + entry.getName()));
                }
                //Else create the file
                else
                {
                    InputStream is = file.getInputStream(entry);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    String uncompressedFileName = destination + entry.getName();
                    Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
                    Files.createFile(uncompressedFilePath);
                    FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
                    while (bis.available() > 0)
                    {
                        fileOutput.write(bis.read());
                    }
                    fileOutput.close();
                    System.out.println("Written :" + entry.getName());
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}