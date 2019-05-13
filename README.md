## WebCrawlie
Simple web crawler

#### Build
`./gradlew build`

#### Usage
##### Simple
`java -jar build/libs/webcrawlie.jar <url>`  
Results will end up in `sitemap.txt`

##### Graph
`java -jar build/libs/webcrawlie.jar <url> graph`  
Results will end up as `sitemap-graph.png`  
_(Requires [graphviz](https://www.graphviz.org))_

###### Issues/ToDo
* Generating the graphing is quite slow for medium sites and times out for large sites 
* Fix link parsing - follows any `href="/*"`, even plain text ones
* Parallelization to speed up crawling
