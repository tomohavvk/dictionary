## Spring webflux playground

- Manual ETL service which allows to load and parse words from external website, then transform(translate using Google
  Translate API) words and then load the transformed(translated) dictionary

Extract words request:

```
curl --location 'http://localhost:8080/api/v1/extract' \
--header 'Content-Type: application/json' \
--data '{
    "url": "https://www.use-in-a-sentence.com/english-words/10000-words/the-most-frequent-10000-words-of-english.html",
    "source_language": "en",
    "filter_by": [
        "</a></li>",
        "/10000-words/"
    ],
    "split_by": [
        {
            "by": "\">",
            "is_take_left": false
        },
        {
            "by": "</a></li>",
            "is_take_left": true
        }
    ]
}'
```

Transform words request:

```
curl --location 'http://localhost:8080/api/v1/transform' \
--header 'Content-Type: application/json' \
--data '{
    "source_language": "en",
    "target_language": "uk"
}'
```

Load translations request:

```
curl --location 'http://localhost:8080/api/v1/load?sourceLanguage=en&targetLanguage=uk&limit=100&offset=0'
```