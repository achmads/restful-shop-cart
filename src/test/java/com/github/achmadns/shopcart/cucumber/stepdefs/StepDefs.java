package com.github.achmadns.shopcart.cucumber.stepdefs;

import com.github.achmadns.shopcart.JhipsterApp;

import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

@WebIntegrationTest(randomPort = true)
@ContextConfiguration(classes = JhipsterApp.class, loader = SpringApplicationContextLoader.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
