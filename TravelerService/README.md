# TravelerService

## APIs

`GET /my/profile`

Response body:

 ```json
{
  "userId": 1,
  "name": "Maria Neri",
  "role": "[CUSTOMER]",
  "address": "Via Mario 34",
  "dateOfBirth": "18/01/1993",
  "telephoneNumber": "3412241202",
  "tickets":[]
}
```

---

`PUT /my/profile`

Request body:

 ```json
{
  "name": "Maria Neri",
  "address": "Via Mario 34",
  "dateOfBirth": "18/01/1993",
  "telephoneNumber": "3412241202"
}
```

Response body:

 ```json
{
  "userId": 1,
  "name": "Maria Neri",
  "role": "[CUSTOMER]",
  "address": "Via Mario 34",
  "dateOfBirth": "18/01/1993",
  "telephoneNumber": "3412241202",
  "tickets":[]
}
```

---

`GET /my/tickets`

`POST /my/tickets`

`GET /admin/travelers`

`GET /admin/traveler/{userID}/profile`

`GET /admin/traveler/{userID}/tickets`