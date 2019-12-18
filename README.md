# Informations for "Bank account kata" project

## The project

* Runs with Java 8+
* Has been developed with Spring WebFlux (Reactive programming)
* Uses H2 as an in-memory RDBMS, can be switched to PostgreSQL if the kata requires a real database in the future.
* Has been developed in TDD methodology, with JUnit 5


## Test the project

Test the project following command line:
```
mvn clean package
```


## Details

* A *user* and his *current account* have been mocked for this project.
* The project doesn't implement more business logic than the given User Stories.
* Some code between Deposit and Withdrawal services could have been refactored, it was intentionally let as it is.
* Money is stored as "Cent", you won't see decimal numbers, don't be surprised.


## Routes

For each of the given user stories you can call the following routes:
* User Story 1, see `DepositControllerTest`, POST method: `/deposit`
* User Story 2, see `WithdrawalControllerTest`, POST method: `/withdrawal`
* User Story 3, see `HistoryControllerTest`, GET method: `/history`

Check also other tests like `DepositServiceTest`, `WithdrawalServiceTest`, `HistoryServiceTest` ...

# Bank account kata

Think of your personal bank account experience. When in doubt, go for the simplest solution

## Requirements

* Deposit and Withdrawal
* Account statement (date, amount, balance)
* Statement printing

The expected result is a service API, and its underlying implementation, that meets the expressed needs.
Nothing more, especially no UI, no persistence.

## User Stories

### US 1:

**In order to** save money

**As a** bank client 

**I want to** make a deposit in my account



### US 2:

**In order to** retrieve some or all of my savings

**As a bank** client

**I want to** make a withdrawal from my account



### US 3:

**In order to** check my operations

**As a** bank client

**I want to** see the history (operation, date, amount, balance) of my operations
