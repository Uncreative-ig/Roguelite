public class Skills
{
	private String name;
	private int basePower;
	private String type;
	private int cooldown;
	private int currentCooldown;
	private int duration;

	public Skills(String name, int basePower, String type, int cooldown, int duration)
	{
		this.name = name;
		this.basePower = basePower;
		this.type = type;
		this.cooldown = cooldown;
		this.currentCooldown = 0;
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public int getBasePower() {
		return basePower;
	}

	public void setBasePower(int newPower) {
		basePower = newPower;
	}

	public String getType() {
		return type;
	}

	public int getCooldown() {
		return cooldown;
	}

	public int currentCooldown() {
		return currentCooldown;
	}
	public int getDuration() {
		return duration;
	}

	public void reduceCooldown()
	{
		if(currentCooldown > 0)
		{
			currentCooldown--;
		}
	}

	public boolean isReady()
	{
		return currentCooldown == 0;
	}

	public void resetCooldown()
	{
		currentCooldown = cooldown;
	}

	public void setCurrentCooldown(int m)
	{
		currentCooldown = m;
	}

}
