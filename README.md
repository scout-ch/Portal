# PBS Portal


## API

### Senden einer Nachricht im Entwicklungssetup (lokal)

```
curl -i -X PUT -H 'Content-Type: application/json' -d '{"title":{"de":"Hallo"},"content":{"de":"Inhalt..."}}' -H 'X-Authorization: DEMO-KEY' http://localhost:8080/api/v1/message
```