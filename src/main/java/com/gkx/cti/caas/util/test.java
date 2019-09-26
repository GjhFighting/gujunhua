package com.gkx.cti.caas.util;

public class test {
	 public static void main(String args[]) {
		 
		 SSHUtil ssh = new SSHUtil();
		 ssh.run("125.91.35.169", "root", "124!@$qweQWE", "ls");
	   }   
}
