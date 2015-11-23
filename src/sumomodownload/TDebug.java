package sumomodownload;

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

}
