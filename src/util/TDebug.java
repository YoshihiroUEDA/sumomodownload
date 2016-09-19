package util;

public class TDebug {
	private boolean debugFlag=false;
	private boolean debugFlagAll=true;

	public void setDebugFlag( boolean flag){
		debugFlag=flag;
	}

	public void setDebugAll( boolean flag){
		debugFlagAll=flag;
	}

	public void dprogress(){
		if(debugFlag && debugFlagAll){
			System.out.print("*");
		}
	}

	public void dPrintln( String str){
		if(debugFlag && debugFlagAll){
			System.out.println(str);
		}

	}

	public void dPrint( String str){
		if (debugFlag &&debugFlagAll){
			System.out.print(str);
		}
	}
	public void cout( String str){
		System.out.print(str);

	}
	public void  coutln ( String s ){
		System.out.println( s);
	}

}
