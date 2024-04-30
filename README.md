## Reactive Spring playground

#### The simple translation service which allows to parse website content, extract words and translate from source to target language and then load the result. The service is use Google Translate API for translation.

The service demonstrates:

- How to use multi modules architecture with maven as build tool
- How to use String WebFlux among with functional endpoints
- How to make simple translation pipeline using Reactor-Kafka
- How to use Avro serialization for Kafka events
- How to perform database migration using Flyway
- How to use Reactive database connectivity (R2DBC)

How to start the service:

- docker-compose up
- optionally export the GOOGLE API KEY using `export TRANSLATION_API_KEY=real_api_key`
    - if the api key will not be provided the fake_api_key will be used among with stub
- mvn package
- run `java -jar launcher/target/launcher-0.0.1-SNAPSHOT.jar`
- execute described below request to start translation

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

Note: The architecture and implementation are not the source of truth, but only my subjective vision. And of course
there is still room for improvement