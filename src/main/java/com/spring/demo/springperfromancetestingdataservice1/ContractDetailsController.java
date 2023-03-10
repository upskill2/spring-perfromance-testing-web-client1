package com.spring.demo.springperfromancetestingdataservice1;

import brave.Span;
import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class ContractDetailsController {

    @Autowired
    Tracer tracer;

    @GetMapping ("/customer/{contractId}/contractDetails")
    public ContractDetails getContractDetails (@PathVariable String contractId) throws InterruptedException {

        Span dbSpan = tracer.nextSpan ().name ("DBLookup");

        try (Tracer.SpanInScope mySpan = tracer.withSpanInScope (dbSpan.start ())
        ) {

            dbSpan.tag ("call", "sql-database");

            Random r = new Random ();
            int multiplier = r.nextInt (5) * 1000;
            System.out.println ("Delay is: " + multiplier);
            Thread.sleep (multiplier);

            dbSpan.annotate ("DB lookup complete!");

        } finally {
            dbSpan.finish ();
        }


        return createContractDetails (contractId);

    }

    private ContractDetails createContractDetails (String contractId) {


        List<ContractDetails> list = new ArrayList<> ();
        list.add (new ContractDetails ("501", "David Rose", "30056"));
        list.add (new ContractDetails ("608", "Ron Boss", "80033"));
        list.add (new ContractDetails ("997", "Fox Box", "123654"));

        return list.stream ().filter (c -> contractId.equals (c.contractId ())).findAny ().orElse (null);
    }

}
