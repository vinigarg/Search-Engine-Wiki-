# Search-Engine-Wiki-
Writen for the Information Retrieval and Extraction Course Spring 2016 at IIIT-H.

## Summary
The Wiki Parser uses java's SAX parser to parse the tags of the Wiki Markup. Main.java is to be executed for parsing and indexing. The Tokenizer and PorterStemmer were included in the project. The title, text, infobox, categories, references and external links are case-folded, tokenized, stemmed and indexed. The search query can be regular words, or can be fielded query like t:lord b:rings . Query.py is the main file to be executed for searching  commands.

## Problem Statement

The given problem was to design and develop a scalable and efficient search engine using the Wikipedia data. Requirements:
* ~50 GB of Wikipedia data (Downloaded compressed file is ~11GB)
* Results obtained in less than a sec (even for long queries)
* Supports field queries (ex: title)
* Index size should be less than 1/4 of the data size.
* You have to build your own indexing mechanism, i.e. you cannot use Nutch or Lucene to index the Wikipedia data. 
* Platform:
  * OS: Preferably Linux
  * Languages: Java/C++/Python

## Approach

By using the SAX parser (Simple API for XML) traverse the file line by line and triggers specific functions whenever there is an opening tag, content, and closing tag. This allows us to parse through the file without having to load the entire structure into memory.

Thus we can decide how many articles to process during the parsing itself.

Using this approach, we extract each Wikipedia article as an object, and then pass it to an Indexer Module. This module analyses the article, takes the necessary text components, tokenizes them, and creates a frequency map of the word and documents it occurs in. The tokenization uses case folding and stemming (Porter Stemmer in Java) so as to cover various word cases.

Now this map can not be stored in memory at once, so dump the map to temporary files for every 5,000 Wikipedia articles, and then merge them together at the end. Then we sort the final file and output compressed part files for us to access in an easy way, and reduce the index size.

The searching takes a query and breaks it down the same way (tokenization, case folding, stemming) and tries to extract the frequency from the index. Then we calculate the TFIDF (Term Frequency - Inverse Document Frequency) for each word and document and generate a ranked list of Wikipedia article titles as the end result.

## Implementation
I wrote the following java program that can parse a Wikipedia dump XML file and index it in index files. These can then be used to search for articles through a simple searching algorithm.

This implementation is very slow and not optimized, it is far from the ideal, but it gets the job done.

Once you clone the project (or download the zip and uncompress it) you will see ans src folder, and some other files.

The src folder contains the packages with .java files for ease of maintenance and modularity.

In order to run the indexer, create 2 folders in the main directory (alongside src).
* files [here the splitted sorted files and splitted id-title map files are stored]
* index [here primary, secondary and tertiary index files, as well as primary and secondary id-title map will be stored]

I provided a sample file with 100 Wikipedia articles that illustrates the concept of indexing and searching.

On Linux open the terminal and run:
    
    javac Main.java
    java Main
    javac Query.java
    java Query

The indexer should start running, it might take a while, and you will get a completion message with time statistics displayed in counts of milliseconds taken.

For testing the searching, you can try 2 types of queries:

* Regular Queries - just plain text
* Fielded Queries - words with specific criteria, like t:lord b:rings, where t: means search in title, b: means search in body. You can use 4 types, namely t: for title, b: for body, c: for category, i: for infobox, r: for reference, e: for external links.

You should see the top 10 results available (maximum) per each query.

## Future works
Use Okapi BM25 for ranking and champion list generation
