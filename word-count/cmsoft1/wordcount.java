package cmsoft1;
/*
多线程读取多个文本文件，并统计文件内单词数量，写入到一个结果文件中
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
public class wordcount {
    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args)
    {
        //文件路径
        File f = new File("src/files");
        final List<File> filePathsList = new ArrayList<File>();
        //返回文件路径内的文件列表
        File[] filePaths = f.listFiles();
        if (filePaths != null) {
            for (File s : filePaths) {
                filePathsList.add(s);
            }
        }

        CountDownLatch latch = new CountDownLatch(filePathsList.size());
        //创建线程池
        ExecutorService pool = Executors.newFixedThreadPool(10);
        //阻塞队列
        BlockingQueue<Future<Map<String, FileInputStream>>> queue =
                new ArrayBlockingQueue<Future<Map<String, FileInputStream>>>(100);

        System.out.println("-------------文件读、写任务开始时间：" + sdf.format(new Date()));
        for (int i = 0; i < filePathsList.size(); i++) {
            File temp = filePathsList.get(i);
            //提交执行
            Future<Map<String,FileInputStream>> future = pool.submit(new FileRead(latch, temp));
            queue.add(future);
            pool.execute(new FileWrite(queue));


        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-------------文件读、写任务结束时间：" + sdf.format(new Date()));
        pool.shutdownNow();
    }




    // 文件读线程
    static class FileRead implements Callable<Map<String, FileInputStream>>
    {
        private CountDownLatch latch;
        private File file;
        private FileInputStream fis = null;
        private Map<String, FileInputStream> fileMap = new HashMap<String, FileInputStream>();
        private int res;


        public FileRead(CountDownLatch latch, File file)
        {
            this.latch = latch;
            this.file = file;
        }

        @Override
        public Map<String, FileInputStream> call() throws Exception
        {
            System.out.println(Thread.currentThread().getName() + " 线程开始读取文件 ：" + file.getName() + " ,时间为 "+ sdf.format(new Date()));
            fis = new FileInputStream(file);
            fileMap.put(file.getName(), fis);
            //int res = doWork(); //统计单词数量
            //countWord();
            System.out.println(Thread.currentThread().getName() + " 线程读取文件 ：" + file.getName() + " 完毕"  + " ,时间为 "+ sdf.format(new Date()));
            latch.countDown();
            return fileMap;

        }

    }


    // 文件写线程
   static class FileWrite implements Runnable
    {
        private String fileName = "";
        private BlockingQueue<Future<Map<String, FileInputStream>>> queue;
        private FileInputStream fis = null;
        private File dirFile = null;

        private BufferedReader br = null;
        private InputStreamReader isr = null;
        private FileWriter fw = null;
        private BufferedWriter bw = null;
        private Map<String, Integer> map2 = new HashMap<>();

        public FileWrite (BlockingQueue<Future<Map<String,FileInputStream>>> queue2)
        {
            this.queue = queue2;
        }

        @Override
        public  void run()
        {
            try {
                wordCountWrite();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        private  void wordCountWrite() throws InterruptedException, ExecutionException {
            Future<Map<String, FileInputStream>> future = queue.take();
            Map<String, FileInputStream> map = future.get(); //获得读取到<文件（名）,文件输入流>

            Set<String> set = map.keySet();
            for (Iterator<String> iter = set.iterator(); iter.hasNext();) {

                fileName = iter.next().toString();
                fis = map.get(fileName);

                System.out.println(Thread.currentThread().getName() + " 线程开始写文件 ：" + fileName  + " ,时间为 "+ sdf.format(new Date()));
                try {
                    isr = new InputStreamReader(fis, "utf-8");
                    br = new BufferedReader(isr);
                    //统计当前文本单词数量
                    String line = "";
                    while((line = br.readLine()) != null){
                        String[] ss = line.split("\\s");
                        for(String s : ss){
                            if(map2.containsKey(s)){
                                map2.put(s, map2.get(s) + 1);
                            }else{
                                map2.put(s, 1);
                            }
                        }
                    }

                    write();

                    //dirFile = new File("d:" + File.separator + "java" + File.separator + fileName);
                    //fw = new FileWriter(dirFile);

                    //String data = "";
                    //bw.write(Thread.currentThread().getName() + " 线程开始写文件++++++++++++");
                    //while ((data = br.readLine()) != null) {
                    //bw.write(data + "\r");
                    // }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bw.close();
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private  void write() throws IOException {
            FileWriter fw = new FileWriter("src/result1.txt",true);
            bw = new BufferedWriter(fw);
            synchronized (fw){
                Set<Map.Entry<String,Integer>> set1 = map2.entrySet();
                int count = 0;
                //结果写入新文件
                for(Map.Entry<String, Integer> entry : set1){
                    //System.out.println(entry);
                    if(count == 0) {
                        bw.write(fileName + " 词频统计: " + '\n');
                    }
                    bw.write(entry.getKey()+" : "+entry.getValue()+" 个 "+'\n');
                    count++;

                }
                System.out.println(Thread.currentThread().getName()+" : "+ fileName +" 统计完毕");
            }


        }
    }
}



