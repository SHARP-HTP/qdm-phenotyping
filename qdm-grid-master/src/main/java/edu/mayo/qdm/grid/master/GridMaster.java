package edu.mayo.qdm.grid.master;

import edu.mayo.qdm.executor.CallbackExecutor;

/**
 */
public interface GridMaster extends CallbackExecutor {

    public GridStatus getStatus();

}
