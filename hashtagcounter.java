import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class hashtagcounter {
    public static void main(String[] args) throws Exception {
        if (args.length == 0)
            return;
     // Open file
        FibonacciHeap fibHeap = new FibonacciHeap();
        HashMap<String, FibonacciNode> hashMap = new HashMap<>();
        String line;
        File filename = new File(args[0]);
        FileInputStream inputStream = new FileInputStream(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Writer writer;
        if (args.length == 1)
            writer = new BufferedWriter(new OutputStreamWriter(System.out));
        else
            writer = new FileWriter(new File(args[1]), false);
        PrintWriter printWriter = new PrintWriter(writer);
        while ((line = bufferedReader.readLine()) != null && !line.equalsIgnoreCase("stop")) {
            line = line.trim();
            if (line.charAt(0) == '#') {
                String[] lineSplits = line.split(" ");
                lineSplits[0] = lineSplits[0].substring(1);
                int value = Integer.parseInt(lineSplits[1]);
                // check whether the tag exists or not
                // if there exists the tag: increase operation
                if (hashMap.containsKey(lineSplits[0])) {
                    fibHeap.increase(hashMap.get(lineSplits[0]), value + hashMap.get(lineSplits[0]).value);
                } else { // if there exists no tag: insert operation, and update hashmap
                    FibonacciNode node = fibHeap.insert(lineSplits[0], value);
                    hashMap.put(lineSplits[0], node);
                }
            } else {
                ArrayList<FibonacciNode> Q = new ArrayList<>();
                int N = Integer.parseInt(line);
                for (int j = 0; j < N; j++) {
                    FibonacciNode currMax = fibHeap.pop();
                    printWriter.print(currMax.key);
                    if (j != N - 1)
                        printWriter.print(",");
                    else
                        printWriter.println();
                    printWriter.flush();
                    Q.add(currMax);
                }
                while (!Q.isEmpty()) {
                    FibonacciNode node = Q.remove(0);
                    hashMap.replace(node.key, fibHeap.insert(node.key, node.value));
                }
            }
        }
        bufferedReader.close();
        printWriter.close();
        writer.close();
        inputStream.close();
    }
}
