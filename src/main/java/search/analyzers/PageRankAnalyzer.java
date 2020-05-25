package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing the 'page rank' of all available webpages.
 * If a webpage has many different links to it, it should have a higher page rank.
 * See the spec for more details.
 */
public class PageRankAnalyzer {
    private IDictionary<URI, Double> pageRanks;

    /**
     * Computes a graph representing the internet and computes the page rank of all
     * available webpages.
     *
     * @param webpages  A set of all webpages we have parsed.
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    public PageRankAnalyzer(ISet<Webpage> webpages, double decay, double epsilon, int limit) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        // Step 1: Make a graph representing the 'internet'
        IDictionary<URI, ISet<URI>> graph = this.makeGraph(webpages);

        // Step 2: Use this graph to compute the page rank for each webpage
        this.pageRanks = this.makePageRanks(graph, decay, limit, epsilon);

        // Note: we don't store the graph as a field: once we've computed the
        // page ranks, we no longer need it!
    }

    /**
     * This method converts a set of webpages into an unweighted, directed graph,
     * in adjacency list form.
     *
     * You may assume that each webpage can be uniquely identified by its URI.
     *
     * Note that a webpage may contain links to other webpages that are *not*
     * included within set of webpages you were given. You should omit these
     * links from your graph: we want the final graph we build to be
     * entirely "self-contained".
     */
    private IDictionary<URI, ISet<URI>> makeGraph(ISet<Webpage> webpages) {
        IDictionary<URI, ISet<URI>> graph = new ChainedHashDictionary<>();
        ISet<URI> uris = new ChainedHashSet<>();
        
        for (Webpage page : webpages) {
            IList<URI> links = page.getLinks();
            ISet<URI> vertices = new ChainedHashSet<>();
            uris.add(page.getUri());
            for (URI link : links) {
                vertices.add(link);
            }
            graph.put(page.getUri(), vertices);
        }
        
        IDictionary<URI, ISet<URI>> culledGraph = new ChainedHashDictionary<>();
        for (KVPair<URI, ISet<URI>> pair : graph) {
            ISet<URI> culledVertices = new ChainedHashSet<>();
            for (URI outbound : pair.getValue()) {
                if (!outbound.equals(pair.getKey()) && uris.contains(outbound)) {
                    culledVertices.add(outbound);
                }
            }
            culledGraph.put(pair.getKey(), culledVertices);
        }
        
        return culledGraph;
    }

    /**
     * Computes the page ranks for all webpages in the graph.
     *
     * Precondition: assumes 'this.graphs' has previously been initialized.
     *
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    private IDictionary<URI, Double> makePageRanks(IDictionary<URI, ISet<URI>> graph,
                                                   double decay,
                                                   int limit,
                                                   double epsilon) {
        // Step 1: The initialize step should go here
        
        double initRank = 1.0 / graph.size();
        IDictionary<URI, Double> oldRanks = new ChainedHashDictionary<>();
        IDictionary<URI, Double> newRanks = new ChainedHashDictionary<>();
        for (KVPair<URI, ISet<URI>> row : graph) {
            oldRanks.put(row.getKey(), initRank);
        }
        for (int i = 0; i < limit; i++) {
            //First, give every web page a new page rank of 0.0
            if (i != 0) {
                for (KVPair<URI, ISet<URI>> pair : graph) {
                    oldRanks.put(pair.getKey(), newRanks.get(pair.getKey()));
                }
            }
            
            for (KVPair<URI, ISet<URI>> source: graph) {
                newRanks.put(source.getKey(), 0.0);
            }
            //Next, we will take the old page rank for every webpage and 
            //equally share it with every web page it links to.
            for (KVPair<URI, ISet<URI>> source: graph) {
                ISet<URI> links = source.getValue();
                if (!links.isEmpty()) {
                    for (URI link : links) {
                        double delta = decay * oldRanks.get(source.getKey()) / links.size();
                        newRanks.put(link, newRanks.get(link) + delta);
                    }
                }else {
                    for (KVPair<URI, ISet<URI>> pair : graph) {
                        double delta = decay * oldRanks.get(source.getKey()) / graph.size();
                        newRanks.put(pair.getKey(), newRanks.get(pair.getKey()) + delta);
                    }
                }
                
            }
            
            for (KVPair<URI, ISet<URI>> pair : graph) {
                newRanks.put(pair.getKey(), newRanks.get(pair.getKey()) + ((1 - decay) / graph.size()));
            }
            
            if (isConvergent(epsilon, oldRanks, newRanks)) {
                return newRanks;
            }
        }
        return newRanks;
    }
    
    private boolean isConvergent(double epsilon, IDictionary<URI, Double> oldRank, 
            IDictionary<URI, Double> newRank) {
        for (KVPair<URI, Double> rank : oldRank) {
            if (Math.abs(rank.getValue() - newRank.get(rank.getKey())) >= epsilon) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the page rank of the given URI.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public double computePageRank(URI pageUri) {
        // Implementation note: this method should be very simple: just one line!
        return this.pageRanks.get(pageUri);
    }
}
