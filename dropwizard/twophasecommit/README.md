
Purpose of this prototype
=========================

To provide a prototype within the context of Dropwizard to perform two phase database commit, a distributed transaction, with extremely high probability of success.


Usage
=====

1. Bootstrap your postgres

    psql -h localhost -U postgres --file src/sql/init.sql

2. Launch the dropwizard

    ./gradlew startdw
    
3. Access http://localhost:8080/tpc/add/anyname-goes-here

4. Verify the name you supplied, `anyname-goes-here` in the above example, is in both databases

    psql -h localhost -U postgres --file src/sql/list.sql


Test
====

Comprehensive test are in TwoPhaseDaoImplTest

* to verify autocommit is true of connection been given to the TwoPhaseDaoImpl
* to verify autocommit is still true of connection when it has done processing adding a name
* to verify the two databases' tables will have same content
* to verify only when both commits are true, the name added is in both databases' tables.

