package net.nee.physics

interface PhysicsTickable {
	/**
	 * @param dt seconds elapsed since last call
	 */
	suspend fun tick(dt: Double)
}