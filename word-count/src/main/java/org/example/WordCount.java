package org.example;
/*
多线程读取多个文本文件，并统计文件内单词数量，写入到一个结果文件中
 */
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCount {
    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        //文件路径
        File f = new File("src/main/resources/");
        final List<File> filePathsList = new ArrayList<File>();
        //返回文件路径内的文件列表
        File[] filePaths = f.listFiles();
        for (File s : filePaths) {
            filePathsList.add(s);
        }

        //创建线程池
        ExecutorService pool = Executors.newFixedThreadPool(filePaths.length);
        //阻塞队列
        BlockingQueue<Future<Map<String, FileInputStream>>> queue =
                new ArrayBlockingQueue<Future<Map<String, FileInputStream>>>(100);
        List<Future<List<String>>> futures = new ArrayList<Future<List<String>>>();
        System.out.println("-------------文件读、写任务开始时间：" + sdf.format(new Date()));
        for (int i = 0; i < filePathsList.size(); i++) {
            File temp = filePathsList.get(i);
            //提交执行
            Future<List<String>> future = pool.submit(new FileRead(temp));
            futures.add(future);
        }
        List<String> allWords = new ArrayList<>();
        for (Future<List<String>> future : futures) {
            try {
                List<String> splits = future.get(10, TimeUnit.SECONDS);
                allWords.addAll(splits);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        System.out.println("-------------文件读任务结束时间：" + sdf.format(new Date()));
        pool.shutdown();

        //统计词频并写入文件
        Map<String, AtomicInteger> wordCount = new HashMap<>();
        for (String word : allWords) {
            if (!wordCount.containsKey(word)) {
                wordCount.put(word, new AtomicInteger(1));
            } else {
                wordCount.get(word).incrementAndGet();
            }
        }
        //打印结果
        System.out.println(wordCount);
        //结果写入文件
        BufferedWriter bufferedWriter = null;
        try {
            FileWriter fileWriter = new FileWriter("./result.txt");
            bufferedWriter = new BufferedWriter(fileWriter);
            Set<Map.Entry<String, AtomicInteger>> countSet = wordCount.entrySet();
            for (Map.Entry<String, AtomicInteger> entry : countSet) {
               bufferedWriter.write(entry.getKey() + " : " + entry.getValue() + " 个 " + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    // 文件读线程
    static class FileRead implements Callable<List<String>> {

        private final File file;
        private FileInputStream fis = null;

        public FileRead(File file) {
            this.file = file;
        }

        @Override
        public List<String> call() throws Exception {
            List<String> result = new ArrayList<>();
            System.out.println(Thread.currentThread().getName() + " 线程开始读取文件 ：" + file.getName() + " ,时间为 " + sdf.format(new Date()));
            fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] split = line.split("\\s");
                    result.addAll(Arrays.asList(split));
                }
            } catch (IOException e) {
                throw new IOException(String.format("%s read failed", file.getName()), e);
            }

            System.out.println(Thread.currentThread().getName() + " 线程读取文件 ：" + file.getName() + " 完毕" + " ,时间为 " + sdf.format(new Date()));
            return result;

        }
    }
}







