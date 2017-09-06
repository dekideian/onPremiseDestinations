package com.NewTestApp.services;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Destinations {
    public static Context getContext(){
	try {
	    return new InitialContext();
	} catch (NamingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }
}
