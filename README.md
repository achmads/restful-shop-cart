# RESTful Shop Cart

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

## Experiment with JHipster and Spring Framework

Bootstrap your project:

```bash
yo jhipster
# generate our domain
yo jhipster:import-jdl shopcart.jh
```

Modify the project as needed.

## What's available

See [Scenario Test](src/test/features/shopping_and_check_out.feature) to see what the rest API does and click [here](src/test/java/com/github/achmadns/shopcart/cucumber/stepdefs/ShoppingSteps.java) to see steps definition.

See [ShoppingEndpointsIntTest](src/test/java/com/github/achmadns/shopcart/web/rest/ShoppingEndpointsIntTest.java) for simple integration test.
