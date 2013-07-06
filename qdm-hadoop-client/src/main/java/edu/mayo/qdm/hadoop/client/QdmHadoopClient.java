package edu.mayo.qdm.hadoop.client;

import edu.mayo.qdm.hadoop.mapreduce.ExecutorMapper;
import edu.mayo.qdm.hadoop.mapreduce.ExecutorReducer;
import edu.mayo.qdm.patient.Patient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class QdmHadoopClient {

    private static final Log log = LogFactory.getLog(QdmHadoopClient.class);

    private static final String PATIENTS_PATH = "patients";

    private Configuration configuration;

    public QdmHadoopClient(String fsUri, String mapRedUri){
        super();
        Configuration configuration = new Configuration();
        configuration.set("fs.default.name", fsUri);
        configuration.set("mapred.job.tracker", mapRedUri);

        this.configuration = configuration;
    }

    public void load(String dataSetId, Iterable<Patient> patients) throws Exception {
        FileSystem fs = FileSystem.get(this.configuration);

        Path patientsPath = new Path(PATIENTS_PATH + "/" + dataSetId);

        FSDataOutputStream out = fs.create(patientsPath);

        for(Patient patient : patients){
            IOUtils.write(patient.toJson() + '\n', out);
        }
    }

    public void execute(String qdmXml) throws Exception {
        Job job = new Job(this.configuration);

        Configuration conf = job.getConfiguration();

        FileSystem fs = FileSystem.get(conf);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setMapperClass(ExecutorMapper.class);
        job.setReducerClass(ExecutorReducer.class);

        Path qdmPath = new Path("qdmxml/CMS127v1.xml");

        DistributedCache.addCacheFile(qdmPath.toUri(), job.getConfiguration());

        FileInputFormat.setInputPaths(job, new Path(PATIENTS_PATH));
        FileOutputFormat.setOutputPath(job, new Path("output"));

        job.setJarByClass(ExecutorMapper.class);

        job.waitForCompletion(true);

        Path out = new Path("output/part-r-00000");

        System.out.println(IOUtils.toString(fs.open(out)));

        fs.delete(new Path("output"), true);

        fs.close();
    }

    public static void main(String[] args) throws Exception {
        QdmHadoopClient client = new QdmHadoopClient("hdfs://localhost:8020", "localhost:9001");
/*
        java.util.Set<Patient> patients = new HashSet<Patient>();
        for(int i = 0;i<10000;i++){
            Patient p = new Patient(Integer.toString(i));
            p.setAge(i);
            patients.add(p);
        }

        client.load("testDataLoad", patients);
*/

        client.execute(null);
    }

}