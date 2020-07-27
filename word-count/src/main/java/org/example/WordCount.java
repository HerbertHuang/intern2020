package org.example;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Haibo Huang
 */
public class WordCount {
    public static void main(String[] args) {
        File dir = new File("/Users/Herbert/GitHub/sucai/intern2020/word-count/src/main/resources");
        File[] files = dir.listFiles();
        assert files != null;
        ExecutorService executorService = Executors.newFixedThreadPool(files.length);
        List<Future<List<String>>> futures = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println(String.format("%s is not file", file.getName()));
                continue;
            }
            Future<List<String>> future = executorService.submit(new FileReader(file));
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
        executorService.shutdown();

        //统计词频
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
    }

    private static class FileReader implements Callable<List<String>> {
        private final File file;
        public FileReader(File file) {
            this.file = file;
        }

        @Override
        public List<String> call() throws Exception {
            List<String> result = new ArrayList<>();
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] split = line.split("\\s");
                    result.addAll(Arrays.asList(split));
                }
            } catch (IOException e) {
                throw new IOException(String.format("%s read failed", file.getName()), e);
            } finally {
                bufferedReader.close();
                inputStream.close();
            }
            return result;
        }
    }
}
