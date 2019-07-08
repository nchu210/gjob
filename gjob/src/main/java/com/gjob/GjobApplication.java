package com.gjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GjobApplication {

	public static void main(String[] args) {
		//添加监听程序
	    SpringApplication sa = new SpringApplication(GjobApplication.class);
	    //sa.addListeners(new StartListener());
        //sa.addListeners(new CloseListener());
        sa.run(args);
	}
	
}
