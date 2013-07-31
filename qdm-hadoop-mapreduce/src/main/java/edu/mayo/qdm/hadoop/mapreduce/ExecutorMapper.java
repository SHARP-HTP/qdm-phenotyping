package edu.mayo.qdm.hadoop.mapreduce;

import edu.mayo.qdm.executor.Executor;
import edu.mayo.qdm.executor.ExecutorFactory;
import edu.mayo.qdm.executor.QdmProcessor;
import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.patient.Patient;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 */
public class ExecutorMapper extends Mapper<LongWritable,Text,Text,Text> {

    private QdmProcessor qdmProcessor;

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Patient p = Patient.fromJson(value.toString());

        Results results = this.qdmProcessor.execute(Arrays.asList(p));
        context.write(new Text(p.getSourcePid()), new Text(Integer.toString(results.get("IPP").size())));
    }

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        Executor executor = ExecutorFactory.instance().getExecutor();

        Path[] localFiles = DistributedCache.getLocalCacheFiles(context.getConfiguration());

        BufferedReader cacheReader = new BufferedReader(
                new FileReader(localFiles[0].toString()));

        String qdmXml = IOUtils.toString(cacheReader);

        this.qdmProcessor = executor.getQdmProcessor(qdmXml, null);
    }
}
