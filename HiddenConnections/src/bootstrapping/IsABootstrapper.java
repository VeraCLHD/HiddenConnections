package bootstrapping;

public class IsABootstrapper extends Bootstrapper {
	
	public IsABootstrapper(){
		this.setType("IS-A");
		this.setPathToSeeds("SEEDS/IS-A_seeds.txt");
	}
	

	@Override
	public void filterConnectionsForType(String type) {
		

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
