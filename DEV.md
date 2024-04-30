
Start translate in background request:
```
curl --location 'http://localhost:8080/api/v1/translate' \
--header 'Content-Type: application/json' \
--data '{
    "url": "https://www.use-in-a-sentence.com/english-words/10000-words/the-most-frequent-10000-words-of-english.html",
    "source_language": "en",
    "target_language": "uk",
    "filter": [
        "</a></li>",
        "/10000-words/"
    ],
    "split": [
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

Load translations request:

```
curl --location 'http://localhost:8080/api/v1/load?sourceLanguage=en&targetLanguage=uk&limit=100&offset=0'
```
