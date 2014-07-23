package intermediate;

import com.sandwich.koan.Koan;
import static com.sandwich.koan.constant.KoanConstants.__;
import static com.sandwich.util.Assert.assertEquals;

public class AboutEquality {
	// This suite of Koans expands on the concepts introduced in beginner.AboutEquality

	@Koan
	public void sameObject() {
		Object a = new Object();
		Object b = a;
		assertEquals(a == b, true);
	}
	
	@Koan
	public void equalObject() {
		Integer a = new Integer(1);
		Integer b = new Integer(1);
		assertEquals(a.equals(b), true);
		assertEquals(b.equals(a), true);
	}
	
	@Koan 
	public void noObjectShouldBeEqualToNull() {
		assertEquals(new Object().equals(null), false);
	}
	
	static class Car {
		@SuppressWarnings("unused")
		private String name = "";
		@SuppressWarnings("unused")
		private int horsepower = 0;
		public Car(String s, int p) {
			name = s; horsepower = p;
		}
		@Override
		public boolean equals(Object other) {
			// Change this implementation to match the equals contract
			// Car objects with same horsepower and name values should be considered equal
			// http://download.oracle.com/javase/6/docs/api/java/lang/Object.html#equals(java.lang.Object)
      if (other == null) {
        return false;
      }
      try {
        Car otherCar = (Car) other;
        if (this.name.equals(otherCar.name) && this.horsepower == otherCar.horsepower) {
          return true;
        } else {
          return false;
        }
      } catch (ClassCastException e) {
        return false;
      }
		}
    @Override
    public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + horsepower;
      return result;
    }
  }
	@Koan 
	public void equalForOwnObjects() {
		Car car1 = new Car("Beetle", 50);
		Car car2 = new Car("Beetle", 50);
		// See line 37 for the task you have to solve
		assertEquals(car1.equals(car2), true);
		assertEquals(car2.equals(car1), true);
	}
	
	@Koan 
	public void unequalForOwnObjects() {
		Car car1 = new Car("Beetle", 50);
		Car car2 = new Car("Porsche", 300);
		// See line 37 for the task you have to solve
		assertEquals(car1.equals(car2), false);
	}
	
	@Koan 
	public void unequalForOwnObjectsWithDifferentType() {
		Car car1 = new Car("Beetle", 50);
		String s = "foo";
		// See line 37 for the task you have to solve
		assertEquals(car1.equals(s), false);
	}
	
	@Koan 
	public void equalNullForOwnObjects() {
		Car car1 = new Car("Beetle", 50);
		// See line 37 for the task you have to solve
		assertEquals(car1.equals(null), false);
	}

	@Koan
	public void ownHashCode() {
		// As a general rule: When you override equals you should override
		// hash code
		// Read the hash code contract to figure out why
		// http://download.oracle.com/javase/6/docs/api/java/lang/Object.html#hashCode()
		
		// Implement a hash code version in line 47 so that the following assertions pass
		Car car1 = new Car("Beetle", 50);
		Car car2 = new Car("Beetle", 50);
		assertEquals(car1.equals(car2), true);
		assertEquals(car1.hashCode() == car2.hashCode(), true);
	}
	
	static class Chicken {
		String color = "green";
		@Override
		public int hashCode() {
			return 4000;
		}
		@Override
		public boolean equals(Object other) {
			if(!(other instanceof Chicken))
				return false;
			return ((Chicken)other).color.equals(color);
		}
	}
	
	@Koan
	public void ownHashCodeImplementationPartTwo() {
		Chicken chicken1 = new Chicken(); chicken1.color = "black";
		Chicken chicken2 = new Chicken();
		assertEquals(chicken1.equals(chicken2), false);
		assertEquals(chicken1.hashCode() == chicken2.hashCode(), true);
		// Does this still fit the hashCode contract? Why?
		// If it's valid why is this still not a good idea?
	}
	
}
