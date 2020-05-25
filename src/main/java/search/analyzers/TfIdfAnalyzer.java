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
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;

    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;
    private IDictionary<URI, Double> documentNorms;

    // Feel free to add extra fields and helper methods.

    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        this.idfScores = this.computeIdfScores(webpages);
        this.documentTfIdfVectors = this.computeAllDocumentTfIdfVectors(webpages);
        this.documentNorms = this.precalculateNorms();
    }

    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    // Note: these private methods are suggestions or hints on how to structure your
    // code. However, since they're private, you're not obligated to implement exactly
    // these methods: feel free to change or modify these methods however you want. The
    // important thing is that your 'computeRelevance' method ultimately returns the
    // correct answer in an efficient manner.

    /**
     * Return a dictionary mapping every single unique word found
     * in every single document to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
        IDictionary<String, Double> scores = new ChainedHashDictionary<>();
        IDictionary<String, Integer> docCount = new ChainedHashDictionary<>();
        
        for (Webpage page : pages) {
            ISet<String> countedWords = new ChainedHashSet<String>();
            for (String word : page.getWords()) {
                if (!countedWords.contains(word)) {
                    countedWords.add(word);
                    
                    if (!docCount.containsKey(word)) {
                        docCount.put(word, 1);
                    }else {
                        docCount.put(word, docCount.get(word) + 1);
                    }
                }
            }
        }
        
        for (KVPair<String, Integer> term : docCount) {
            scores.put(term.getKey(), Math.log(pages.size() / ((double) term.getValue())));
        }
        
        return scores;
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * The input list represents the words contained within a single document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
        IDictionary<String, Integer> wordCounter = new ChainedHashDictionary<>();
        IDictionary<String, Double> scores = new ChainedHashDictionary<>();
        
        for (String word: words) {
            if (wordCounter.containsKey(word)) {
                wordCounter.put(word, wordCounter.get(word) + 1);
            }else {
                wordCounter.put(word, 1);
            }
        }
        
        for (KVPair<String, Integer> word: wordCounter) {
            scores.put(word.getKey(), ((double) word.getValue())/words.size());
        }
        
        return scores;
    }

    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        // Hint: this method should use the idfScores field and
        // call the computeTfScores(...) method.
        IDictionary<URI, IDictionary<String, Double>> vectors = new ChainedHashDictionary<>();
        
        for (Webpage page : pages) {
            IDictionary<String, Double> tfScores = this.computeTfScores(page.getWords());
            IDictionary<String, Double> calculatedScores = new ChainedHashDictionary<>();
            
            for (KVPair<String, Double> pair: tfScores) {
                double idf = 0.0;
                if (idfScores.containsKey(pair.getKey())) {
                    idf = idfScores.get(pair.getKey());
                }
                calculatedScores.put(pair.getKey(), pair.getValue() * idf);
            }
            vectors.put(page.getUri(), calculatedScores);
        }
        return vectors;
    }

    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        // Note: The pseudocode we gave you is not very efficient. When implementing,
        // this method, you should:
        //
        // 1. Figure out what information can be precomputed in your constructor.
        //    Add a third field containing that information.
        //
        // 2. See if you can combine or merge one or more loops.
        
        IDictionary<String, Double> docuVector = this.documentTfIdfVectors.get(pageUri);
        IDictionary<String, Double> queryVector = this.computeQueryVector(query);
        
        double numerator = 0.0;
        for (String term : query) {
            double docWordScore = 0.0;
            if (docuVector.containsKey(term)) {
                docWordScore = docuVector.get(term);
            }
            double queryWordScore = queryVector.get(term);
            numerator += docWordScore * queryWordScore;
        }
        
        double denominator = this.documentNorms.get(pageUri) * this.norm(queryVector);
        
        if (denominator != 0) {
            return numerator / denominator;
        }    
        return 0.0;
    }
    
    private IDictionary<URI, Double> precalculateNorms(){
        IDictionary<URI, Double> norms = new ChainedHashDictionary<>();
        for (KVPair<URI, IDictionary<String, Double>> pair : documentTfIdfVectors){
            norms.put(pair.getKey(), this.norm(pair.getValue()));
        }
        return norms;
    }

    private double norm(IDictionary<String, Double> vector) {
        double output = 0.0;
        for (KVPair<String, Double> pair: vector) {
            double score = pair.getValue();
            output += score * score;
        }
        return Math.sqrt(output);
    }
    
    private IDictionary<String, Double> computeQueryVector(IList<String> query){
        IDictionary<String, Double> queryVector = new ChainedHashDictionary<>();
        IDictionary<String, Double> queryTfScores = this.computeTfScores(query);
        
        for (String term : query) {
            queryVector.put(term, queryTfScores.get(term) * this.idfScores.get(term));
        }
        return queryVector;
    }
}
