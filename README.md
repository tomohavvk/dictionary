## Spring webflux playground

- Dictionary service allows to parse words from website based on rules and then translate words to target language using Google Translate API. The translation result is stored into the database

Parse words request example:
```json
curl --location 'http://localhost:8080/api/v1/parse' \
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

Translate words request example:
```json
curl --location 'http://localhost:8080/api/v1/translate' \
--header 'Content-Type: application/json' \
--data '{
    "source_language": "en",
    "target_language": "uk"
}'
```