package JavaSource.com.apress.expertspringmvc.flight.domain;

public class Name {

    private String first;
    private String middle;
    private String last;
    
    public Name() { }

    public Name(String first, String middle, String last) {
        this.first = first;
        this.middle = middle;
        this.last = last;
    }

    public String getFirst() {
        return first;
    }
    public void setFirst(String first) {
        this.first = first;
    }
    public String getLast() {
        return last;
    }
    public void setLast(String last) {
        this.last = last;
    }
    public String getMiddle() {
        return middle;
    }
    public void setMiddle(String middle) {
        this.middle = middle;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (! (obj instanceof Name)) return false;
        Name name = (Name) obj;
        return ((first == null ? name.first == null : first.equals(name.first)) &&
                (middle == null ? name.middle == null : middle.equals(name.middle)) &&
                (last == null ? name.last == null : last.equals(name.last)));
    }

    @Override
    public int hashCode() {
        String fullName = toString();
        return fullName.toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (first != null) {
            sb.append(first);
            sb.append(" ");
        }
        if (middle != null) {
            sb.append(middle);
            sb.append(" ");
        }
        if (last != null) {
            sb.append(last);
        }
        return sb.toString();
    }
    
}
