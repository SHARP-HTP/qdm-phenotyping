package edu.mayo.qdm.hadoop.mapreduce;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 */
public class ExecutorReducer extends Reducer<Text,Text,Text,Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String combo = StringUtils.join(values.iterator(), ",");

        context.write(key, new Text(combo));
    }
}
