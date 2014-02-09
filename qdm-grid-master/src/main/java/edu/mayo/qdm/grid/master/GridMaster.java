package edu.mayo.qdm.grid.master;

import edu.mayo.qdm.executor.Executor;

/**
 */
public interface GridMaster extends Executor {

    public GridStatus getStatus();

}
