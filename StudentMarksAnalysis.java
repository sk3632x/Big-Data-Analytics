import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class StudentMarksAnalysis {

    public static class MarksMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private Text studentKey = new Text();
        private IntWritable marksValue = new IntWritable();

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString().trim();
            if (line.isEmpty()) return;

            String[] fields = line.split(",");
            if (fields.length == 4) {
                String studentIdName = fields[0].trim() + " - " + fields[1].trim();
                int marks = Integer.parseInt(fields[3].trim());

                studentKey.set(studentIdName);
                marksValue.set(marks);
                context.write(studentKey, marksValue);
            }
        }
    }

    public static class MarksReducer extends Reducer<Text, IntWritable, Text, Text> {
        private Text resultSummary = new Text();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int totalMarks = 0;
            int subjectCount = 0;

            for (IntWritable val : values) {
                totalMarks += val.get();
                subjectCount++;
            }

            double average = (subjectCount > 0) ? (double) totalMarks / subjectCount : 0.0;
            String grade = (average >= 80) ? "A" : (average >= 60) ? "B" : (average >= 40) ? "C" : "F";

            String output = String.format("Total: %d | Avg: %.2f | Grade: %s", totalMarks, average, grade);
            resultSummary.set(output);
            context.write(key, resultSummary);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: StudentMarksAnalysis <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Student Examination Marks Analysis");

        job.setJarByClass(StudentMarksAnalysis.class);
        job.setMapperClass(MarksMapper.class);
        job.setReducerClass(MarksReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}