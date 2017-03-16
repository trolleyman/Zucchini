package game.world.entity.update;

import game.world.Team;
import game.world.entity.*;
import game.world.entity.weapon.Handgun;
import game.world.entity.weapon.MachineGun;
import game.world.entity.weapon.Weapon;
import org.joml.Vector2f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class EntityUpdateReflect<E extends Entity> {
	private Class<E> entityClass;
	private int id;
	
	public HashMap<Field, Object> values = new HashMap<>();
	
	public EntityUpdateReflect(Class<E> _entityClass, int _id) {
		this.entityClass = _entityClass;
		this.id = _id;
	}
	
	/**
	 * Gets the id associated with this EntityUpdate
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Finds the field specified in the class specified at construction time
	 * @param fieldName The field name
	 * @return null if the field was not found
	 */
	public Field findDeclaredField(String fieldName) {
		Class<? extends Entity> clazz = entityClass;
		while (true) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields)
				if (field.getName().equals(fieldName))
					return field;
			
			if (clazz.equals(Entity.class)) // Stop looping up at Entity
				break;
			clazz = (Class<? extends Entity>) clazz.getSuperclass();
		}
		return null;
	}
	
	/**
	 * Queues up a set property action.
	 * @param fieldName The field to set
	 * @param value The value to set it with. This handles boxing and unboxing (Float vs float)
	 * @throws NoSuchFieldException if the field does not exist
	 * @throws IllegalAccessException if the field is transient, static or final.
	 * @throws ClassCastException if the field type and the value type don't match.
	 */
	public void setProperty(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException, ClassCastException {
		// Check if the property exists on the class provided
		Field field = this.findDeclaredField(fieldName);
		int mods = field.getModifiers();
		if (Modifier.isTransient(mods))
			throw new IllegalAccessException(fieldName + " is transient");
		if (Modifier.isStatic(mods))
			throw new IllegalAccessException(fieldName + " is static");
		if (Modifier.isFinal(mods))
			throw new IllegalAccessException(fieldName + " is final");
		
		// Type check
		Class<?> fieldType = field.getType();
		Class<?> valueType = value.getClass();
		
		if (fieldType.isPrimitive()) {
			if (fieldType.equals(byte.class) && !valueType.equals(Byte.class))
				throw new ClassCastException();
			if (fieldType.equals(short.class) && !valueType.equals(Short.class))
				throw new ClassCastException();
			if (fieldType.equals(int.class) && !valueType.equals(Integer.class))
				throw new ClassCastException();
			if (fieldType.equals(long.class) && !valueType.equals(Long.class))
				throw new ClassCastException();
			if (fieldType.equals(float.class) && !valueType.equals(Float.class))
				throw new ClassCastException();
			if (fieldType.equals(double.class) && !valueType.equals(Double.class))
				throw new ClassCastException();
			if (fieldType.equals(boolean.class) && !valueType.equals(Boolean.class))
				throw new ClassCastException();
			if (fieldType.equals(char.class) && !valueType.equals(Character.class))
				throw new ClassCastException();
		} else {
			// Ensure that valueType is a subclass of fieldType
			valueType.asSubclass(fieldType);
		}
		
		values.put(field, value);
	}
	
	/**
	 * Updates the specified Entity with the information provided in the class
	 * @param e The entity to be modified
	 * @return true if the entity was successfully updated, false otherwise
	 */
	public boolean updateEntity(Entity e) {
		try {
			// Ensure that e is a subclass of entityClass
			e.getClass().asSubclass(entityClass);
			
			// Try and set the properties to the values specified
			for (Map.Entry<Field, Object> entry : values.entrySet()) {
				Field field = entry.getKey();
				Object value = entry.getValue();
				
				Class<?> fieldType = field.getType();
				
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				
				if (!fieldType.isPrimitive()) {
					field.set(e, value);
				} else {
					if (fieldType.equals(byte.class))
						field.setByte(e, (Byte) value);
					else if (fieldType.equals(short.class))
						field.setShort(e, (Short) value);
					else if (fieldType.equals(int.class))
						field.setInt(e, (Integer) value);
					else if (fieldType.equals(long.class))
						field.setLong(e, (Long) value);
					else if (fieldType.equals(float.class))
						field.setFloat(e, (Float) value);
					else if (fieldType.equals(double.class))
						field.setDouble(e, (Double) value);
					else if (fieldType.equals(boolean.class))
						field.setBoolean(e, (Boolean) value);
					else if (fieldType.equals(char.class))
						field.setChar(e, (Character) value);
					else {
						System.err.println("Warning: This should never happen.");
						new Throwable().printStackTrace();
						return false;
					}
				}
				
				// Reset accessibility
				field.setAccessible(accessible);
			}
			
			return true;
		} catch (ClassCastException ex) {
			return false;
		} catch (IllegalAccessException ex) {
			System.err.println("Warning: This should never happen.");
			throw new RuntimeException(ex);
		}
	}
	
	public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
		Entity mgun = new MachineGun(new Vector2f(), 100);
		Entity hgun = new Handgun(new Vector2f(), 100);
		Entity player = new Player(Team.FIRST_PLAYER_TEAM, new Vector2f(), "test", null);
		
		EntityUpdateReflect positionUpdate = new EntityUpdateReflect<>(Entity.class, Entity.INVALID_ID);
		positionUpdate.setProperty("position", new Vector2f(1.0f, 2.0f));
		
		// Apply update
		System.out.println("mgun: " + positionUpdate.updateEntity(mgun));
		System.out.println("hgun: " + positionUpdate.updateEntity(hgun));
		System.out.println("player: " + positionUpdate.updateEntity(player));
		
		System.out.println("mgun pos: " + mgun.position);
		System.out.println("hgun pos: " + hgun.position);
		System.out.println("player pos: " + player.position);
		
		// Generate update to position property, but restrict it to Weapons
		EntityUpdateReflect weaponUpdate = new EntityUpdateReflect<>(Weapon.class, Entity.INVALID_ID);
		weaponUpdate.setProperty("position", new Vector2f(-1.0f, -2.0f));
		
		// Apply update
		System.out.println("mgun: " + weaponUpdate.updateEntity(mgun));
		System.out.println("hgun: " + weaponUpdate.updateEntity(hgun));
		System.out.println("player: " + weaponUpdate.updateEntity(player));
		
		System.out.println("mgun pos: " + mgun.position);
		System.out.println("hgun pos: " + hgun.position);
		System.out.println("player pos: " + player.position);
	}
}
