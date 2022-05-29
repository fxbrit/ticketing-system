# LoginService

## APIs

`POST /user/login`

Request body:

 ```json
{
  "username": "test",
  "password": "PassWord123?!"
}
 ```

Response body:

 ```json
{
  "username": "1",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOlsiQ1VTVE9NRVIiXSwiaWF0IjoxNjUzMDY0MTM3LCJleHAiOjE2NTMwNjc3Mzd9.r_YUT-vypxzVxgqZPMhVK_JXmq55XVYXk2KgUtuSHMw"
}
```

---

`POST /user/register`

Request body:

 ```json
{
  "username": "test",
  "password": "PassWord123?!",
  "email": "testing@ad.com"
}
 ```

Response body:

 ```json
{
  "provisional_id": "7c39accd-4377-4d55-a312-3cedd7104d37",
  "email": "testing@ad.com"
}
```

---

`POST /user/validate`

Request body:

 ```json
{
  "provisionalId": "7c39accd-4377-4d55-a312-3cedd7104d37",
  "activationCode": "bFT45Rhhm1"
}
 ```

Response body:

 ```json
{
  "userId": 1,
  "username": "test",
  "email": "testing@ad.com"
}
```