# RESTful money transfer service



It's based on Spark-java framework, h2 in-memory database, pure JDBC transactions and provide 5 types of operations:
- Creating client account with chosen currency unit
- Removing client account
- getting all user's accounts with details like number, balance, currency
- Refilling user account in any of available currency
- Transfer funds from one account to another account

Currency rates are calculated based on https://www.cbr-xml-daily.ru/ API.

Every transaction is presented as RESTful API:

**Create user's account:**
>- method: POST
>- url: /account
>- request body example: {"user":
{"firstName":"Ivan",
"secondName":"Ivanov","patronymicName":"Ivanovich","passportNum":"1234567890","birthdate":"Oct 20, 1990"},"currency":"RUB"}

**Remove user's account:**
>- method: DELETE
>- url: /account
>- request query example: first_name=Ivan&second_name=Ivanov&patronymic_name=Ivanovich&passport=1234567890&birthdate=1990-10-20&account_number=4444555566667777

**Get all user's accounts:**
>- method: GET
>- url: /accounts
>- request query example: first_name=Ivan&second_name=Ivanov&patronymic_name=Ivanovich&passport=1234567890&birthdate=1990-10-20

**Refill user;s account:**
>- method: PUT
>- url: /top_up_account
>- request body example: {"accountNum":1111222233334444,"targetAccountNum":5555666677778888,"amount":1000,"currency":"EUR"}

**Transfer money to a target account:**
>- method: PUT
>- url: /transfer_money
>- request body example: {"user":{"firstName":"Ivan","secondName":"Ivanov","patronymicName":"Ivanovich","passportNum":"1234567890","birthdate":"Oct 20, 1990"},"accountNum":1111222233334444,"targetAccountNum":5555666677778888,"amount":1000,"currency":"USD"}
