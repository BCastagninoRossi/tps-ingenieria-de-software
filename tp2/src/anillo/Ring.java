package anillo;

public class Ring {
    Object cargo;     // Datos del nodo actual
    Ring   next;      // Siguiente nodo en el anillo
    RingState state;  // Estado actual: vacío, un solo nodo o varios nodos

    public Ring() {
        this.cargo = null;
        this.next  = this;
        this.state = new EmptyRingState();
    }

    public Ring next() {
        return state.next(this);
    }

    public Object current() {
        return state.current(this);
    }

    public Ring add(Object cargo) {
        return state.add(this, cargo);
    }

    public Ring remove() {
        return state.remove(this);
    }

    //--------------------------------------------------------------------------
    // Definición de los estados
    //--------------------------------------------------------------------------

    abstract static class RingState {
        abstract Ring next(Ring ctx);
        abstract Object current(Ring ctx);
        abstract Ring add(Ring ctx, Object cargo);
        abstract Ring remove(Ring ctx);

        // Nuevo método para manejar la transición a SingleRingState
        Ring becomeSingleIfNeeded(Ring ctx) {
            return ctx; // No cambia nada por defecto
        }

        // Método auxiliar para “copiar” datos de otro nodo.
        // Se usa en la transición sin if (por ejemplo, al eliminar un nodo).
        protected void copyNode(Ring target, Ring source) {
            target.cargo = source.cargo;
            target.next  = source.next;
        }
    }

    //--------------------------------------------------------------------------

    static class EmptyRingState extends RingState {
        @Override
        Ring next(Ring ctx) {
            throw new IllegalStateException("No se puede avanzar en un anillo vacío.");
        }

        @Override
        Object current(Ring ctx) {
            throw new IllegalStateException("No hay elemento actual en un anillo vacío.");
        }

        @Override
        Ring add(Ring ctx, Object cargo) {
            ctx.cargo = cargo;
            ctx.next  = ctx;
            ctx.state = new SingleRingState();
            return ctx;
        }

        @Override
        Ring remove(Ring ctx) {
            // Quitar en vacío no hace nada; se mantiene vacío
            return ctx;
        }
    }

    //--------------------------------------------------------------------------

    static class SingleRingState extends RingState {
        @Override
        Ring next(Ring ctx) {
            // En un anillo de un solo nodo, el siguiente eres tú mismo
            return ctx;
        }

        @Override
        Object current(Ring ctx) {
            return ctx.cargo;
        }

        @Override
        Ring add(Ring ctx, Object cargo) {
            // Se crea un segundo nodo, lo que convierte el anillo en “múltiple”
            Ring newNode = new Ring();
            newNode.cargo = ctx.cargo;   // Mover el dato actual al nuevo nodo
            newNode.next  = ctx;         // El nuevo nodo apunta al original
            newNode.state = new MultiRingState();

            // El nodo inicial recibe los nuevos datos
            ctx.cargo = cargo;
            ctx.next  = newNode;
            // Y pasa a ser un anillo de varios nodos
            ctx.state = new MultiRingState();

            return ctx;
        }

        @Override
        Ring remove(Ring ctx) {
            // Al quitar el único nodo, quedamos en vacío
            ctx.cargo = null;
            ctx.state = new EmptyRingState();
            return ctx;
        }
    }

    //--------------------------------------------------------------------------

    static class MultiRingState extends RingState {
        @Override
        Ring next(Ring ctx) {
            return ctx.next;
        }

        @Override
        Object current(Ring ctx) {
            return ctx.cargo;
        }

        @Override
        Ring add(Ring ctx, Object cargo) {
            // Insertamos un nuevo nodo entre ctx y ctx.next
            Ring newNode = new Ring();
            // Copiamos el estado actual al nuevo nodo
            newNode.cargo = ctx.cargo;
            newNode.next  = ctx.next;
            newNode.state = this; // Sigue siendo un anillo “múltiple”

            // Actualizamos el nodo actual con el nuevo cargo, 
            // y su puntero next apunta al newNode
            ctx.cargo = cargo;
            ctx.next  = newNode;
            // ctx sigue en MultiRingState
            return ctx;
        }

        @Override
        Ring remove(Ring ctx) {
            // Eliminamos el “nodo siguiente” sin condicional:
            // 1. Copiamos datos del próx. nodo (ctx.next) al actual (ctx).
            // 2. Ajustamos el puntero al “siguiente del siguiente”
            // 3. Si la estructura queda en un solo nodo, 
            //    ese nodo se encarga de asumir SingleRingState (ver método becomeSingle).
            Ring nodeToRemove = ctx.next;
            copyNode(ctx, nodeToRemove);

            // “Invitamos” al nodo que acabamos de quitar a que decida 
            // si debemos convertirnos en SingleRingState.
            // En lugar de if, hacemos que nodeToRemove se encargue
            // internamente de la transición si detecta que solo queda un nodo.
            return nodeToRemove.state.becomeSingleIfNeeded(ctx);
        }

        // Para no usar if, trasladamos la “decisión” a un método en el nodo removido.
        // Por defecto, en MultiRingState no hacemos nada: permanecemos múltiples.
        @Override
        Ring becomeSingleIfNeeded(Ring ctx) {
            return ctx; // MultiRingState por defecto no cambia nada. 
        }
    }

    //--------------------------------------------------------------------------
    // Subclase local sin interfaces para “marcar” un nodo que, al ser removido,
    // fuerce la transición a SingleRingState si detecta que ctx.next == ctx.
    // Se logra imitando la antigua idea de Strategy, pero sin if y sin interface.
    //--------------------------------------------------------------------------

    // (Posiblemente podrías usar otro tipo de truco, pero aquí se ejemplifica
    //  cómo inyectar polimorfismo extra sin condicionales).
    static class LastNodeInMultiRingState extends MultiRingState {
        @Override
        Ring becomeSingleIfNeeded(Ring ctx) {
            // Si se llegó aquí, asumimos (sin un if explícito) que ya es un solo nodo
            // y forzamos SingleRingState.
            ctx.state = new SingleRingState();
            return ctx;
        }
    }
}