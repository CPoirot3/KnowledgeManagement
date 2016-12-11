package com.bupt.poirot.jena.datasets;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.Lock;
import org.apache.jena.sparql.ARQException;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Transactional;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.NodeUtils;

import java.util.Iterator;

public class KnowledgeDatasetImpl implements Dataset {
    protected DatasetGraph dsg = null ;
    // Allow for an external transactional.
    private Transactional transactional = null ;
    private Model dftModel = null ;

    /** Wrap an existing DatasetGraph */
    public static Dataset wrap(DatasetGraph datasetGraph) {
        return new KnowledgeDatasetImpl(datasetGraph) ;
    }

    protected KnowledgeDatasetImpl(DatasetGraph dsg) {
        this(dsg,  (dsg.supportsTransactions() ? dsg : null)) ;
    }

    protected KnowledgeDatasetImpl(DatasetGraph dsg, Transactional transactional) {
        this.dsg = dsg;
        this.transactional = transactional ;
    }

    /** Create a Dataset with the model as default model.
     *  Named models must be explicitly added to identify the storage to be used.
     */
    public KnowledgeDatasetImpl(Model model)
    {
        this.dsg = DatasetGraphFactory.create(model.getGraph()) ;
        this.transactional = dsg ;
        dftModel = model ;
    }

    /** Create a Dataset with a copy of the structure of another one,
     * while sharing the graphs themselves.
     */
    @Deprecated
    public KnowledgeDatasetImpl(Dataset ds)
    {
        this(DatasetGraphFactory.cloneStructure(ds.asDatasetGraph())) ;
    }

    @Override
    public Model getDefaultModel() {
        if ( dftModel == null )
            dftModel = ModelFactory.createModelForGraph(dsg.getDefaultGraph()) ;
        return dftModel ;
    }

    @Override
    public Lock getLock() { return dsg.getLock() ; }

    @Override
    public Context getContext() {
        return dsg.getContext();
    }

    @Override
    public boolean supportsTransactions() {
        return dsg.supportsTransactions() ;
    }

    @Override
    public boolean supportsTransactionAbort() {
        return dsg.supportsTransactionAbort() ;
    }

    @Override
    public void begin(ReadWrite mode) {
        checkTransactional();
        transactional.begin(mode);
    }

    /** Say whether a transaction is active */
    @Override
    public boolean isInTransaction() {
        checkTransactional();
        return transactional != null && transactional.isInTransaction();
    }

    @Override
    public void commit() {
        checkTransactional();
        transactional.commit();
        dftModel = null;
    }

    @Override
    public void abort() {
        checkTransactional();
        transactional.abort();
        dftModel = null;
    }

    @Override
    public void end() {
        checkTransactional();
        transactional.end();
        dftModel = null;
    }

    private void checkTransactional() {
        if ( ! supportsTransactions() )
            throw new UnsupportedOperationException("Transactions not supported") ;
    }

    @Override
    public DatasetGraph asDatasetGraph() { return dsg ; }

    @Override
    public Model getNamedModel(String uri) {
        checkGraphName(uri) ;
        Node n = NodeFactory.createURI(uri) ;
        return graph2model(dsg.getGraph(n)) ;
    }

    @Override
    public void addNamedModel(String uri, Model model) {
        checkGraphName(uri) ;
        Node n = NodeFactory.createURI(uri) ;
        dsg.addGraph(n, model.getGraph()) ;
    }

    @Override
    public void removeNamedModel(String uri) {
        checkGraphName(uri) ;
        Node n = NodeFactory.createURI(uri) ;
        dsg.removeGraph(n) ;
    }

    @Override
    public void replaceNamedModel(String uri, Model model) {
        // Assumes single writer.
        checkGraphName(uri) ;
        Node n = NodeFactory.createURI(uri) ;
        dsg.removeGraph(n) ;
        dsg.addGraph(n, model.getGraph() ) ;
    }

    @Override
    public void setDefaultModel(Model model) {
        if ( model == null )
            model = ModelFactory.createDefaultModel() ;
        dsg.setDefaultGraph(model.getGraph()) ;
    }

    @Override
    public boolean containsNamedModel(String uri) {
        // Does not touch the cache.
        checkGraphName(uri) ;
        Node n = NodeFactory.createURI(uri) ;
        return dsg.containsGraph(n) ;
    }

    @Override
    public Iterator<String> listNames() {
        return NodeUtils.nodesToURIs(dsg.listGraphNodes()) ;
    }

    @Override
    public void close() {
        dsg.close() ;
        dftModel = null ;
    }

    protected Model graph2model(final Graph graph) {
        return ModelFactory.createModelForGraph(graph);
    }

    protected static void checkGraphName(String uri) {
        if ( uri == null )
            throw new ARQException("null for graph name");
    }
}
