package anillo;

public class Ring {
    private Object cargo;
    private Ring next;

    public Ring() {
        this.cargo = null;
        this.next = this;  // Points to itself when empty
    }

    public Ring next() {
        if (this.cargo == null) {
            throw new IllegalStateException("Cannot call next() on an empty ring");
        } else {
            return this.next;
        }
    }

    public Object current() {
        if (this.cargo == null) {
            throw new IllegalStateException("Cannot call current() on an empty ring");
        } else {
            return this.cargo;
        }
    }

    public Ring add(Object cargo) {
        if (this.cargo == null) {
            // Empty ring case
            this.cargo = cargo;
            this.next = this;
            return this;
        } else {
            // Non-empty ring case
            Ring newNode = new Ring();
            newNode.cargo = this.cargo;
            newNode.next = this.next;

            this.cargo = cargo;
            this.next = newNode;

            return this;
        }
    }

    public Ring remove() {
        if (this.cargo == null) {
            // Empty ring - nothing to remove
            return this;
        } else if (this.next == this) {
            // Only one node - make it empty
            this.cargo = null;
            return this;
        } else {
            // Multiple nodes - remove current and make next the new current
            this.cargo = this.next.cargo;
            this.next = this.next.next;
            return this;
        }
    }
}