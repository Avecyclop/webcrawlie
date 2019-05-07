## WebCrawlie
Simple web crawler

#### Build
`./gradlew build`

#### Usage
`java -jar build/libs/webcrawlie.jar <url>`  
Results will end up in `sitemap.txt`

###### Issues/ToDo
* Better presentation than a text file (something that can graph a tree)
* Fix link parsing - follows any `href="/*"`, even plain text ones
* Parallelization to speed up crawling
