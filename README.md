# m'versioner

Manage [Fresh8](http://fresh8gaming.com/) autoscalable services like a gentleman.

## Usage
### POST /versions/list
Example response:
```js
{
    "versions": [
        {
            "service": "first-service", 
            "version": "4.0.0-b.186"
        }, 
        {
            "service": "another-service", 
            "version": "1.0.0-b.33"
        },
    ]
}
```
