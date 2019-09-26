package com.gkx.cti.caas.util;

import com.gkx.cti.caas.pojo.Server;

public class Thread implements Runnable {
	   private Thread t;
	   private String threadName;
	   
	   public Thread( String name) {
	      threadName = name;
	   }
	   
	   public String run(SSHUtil ssh,Server server,String serverip,String cmdString) {
		   System.out.println("线程工作");
		   return ssh.run(serverip, server.getRootname(), server.getPwd(), cmdString);
	   }
	   
	   public void start () {
	   }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
	 


	  
	