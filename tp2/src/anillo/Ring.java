package anillo;

public class Ring {
    // Los datos almacenados en el nodo actual
    Object cargo;

    // Referencia al siguiente nodo en el anillo
    Ring next;

    // El estado actual del anillo (vacío o no vacío)
    RingState state;

    // El constructor inicializa el anillo como vacío
    public Ring() {
        this.cargo = null; // No hay datos en el anillo inicialmente
        this.next = this;  // Apunta a sí mismo cuando está vacío
        this.state = new EmptyRingState(); // Comienza en el estado vacío
    }

    // Delegar la operación next() al estado actual
    public Ring next() {
        return state.next(this);
    }

    // Delegar la operación current() al estado actual
    public Object current() {
        return state.current(this);
    }

    // Delegar la operación add() al estado actual
    public Ring add(Object cargo) {
        return state.add(this, cargo);
    }

    // Delegar la operación remove() al estado actual
    public Ring remove() {
        return state.remove(this);
    }
}

// Interfaz para el estado del anillo
interface RingState {
    Ring next(Ring context); // Obtener el siguiente nodo
    Object current(Ring context); // Obtener los datos del nodo actual
    Ring add(Ring context, Object cargo); // Agregar un nuevo nodo
    Ring remove(Ring context); // Eliminar el nodo actual
}

// Representa el estado de un anillo vacío
class EmptyRingState implements RingState {
    @Override
    public Ring next(Ring context) {
        // No se puede navegar al siguiente nodo en un anillo vacío
        throw new IllegalStateException("No se puede llamar a next() en un anillo vacío");
    }

    @Override
    public Object current(Ring context) {
        // No se pueden obtener los datos del nodo actual en un anillo vacío
        throw new IllegalStateException("No se puede llamar a current() en un anillo vacío");
    }

    @Override
    public Ring add(Ring context, Object cargo) {
        // Agregar el primer elemento al anillo
        context.cargo = cargo; // Establecer los datos para el nodo actual
        context.next = context; // El siguiente nodo apunta a sí mismo
        context.state = new NonEmptyRingState(); // Transición al estado no vacío
        return context;
    }

    @Override
    public Ring remove(Ring context) {
        // Eliminar de un anillo vacío no hace nada
        return context;
    }
}

// Representa el estado de un anillo no vacío
class NonEmptyRingState implements RingState {
    @Override
    public Ring next(Ring context) {
        // Navegar al siguiente nodo en el anillo
        return context.next;
    }

    @Override
    public Object current(Ring context) {
        // Devolver los datos del nodo actual
        return context.cargo;
    }

    @Override
    public Ring add(Ring context, Object cargo) {
        // Crear un nuevo nodo con los datos del nodo actual
        Ring newNode = new Ring();
        newNode.cargo = context.cargo; // Copiar los datos del nodo actual
        newNode.next = context.next; // Enlazar el nuevo nodo al siguiente nodo
        newNode.state = new NonEmptyRingState(); // El nuevo nodo está no vacío

        // Actualizar el nodo actual con los nuevos datos
        context.cargo = cargo;
        context.next = newNode; // Enlazar el nodo actual al nuevo nodo

        return context;
    }

    @Override
    public Ring remove(Ring context) {
        // Delegar la lógica de eliminación a la estrategia apropiada
        return getRemoveStrategy(context).execute(context);
    }

    // Determina la estrategia de eliminación apropiada según la estructura del anillo
    private RemoveStrategy getRemoveStrategy(Ring context) {
        // Array de estrategias: eliminación de múltiples nodos y eliminación de un solo nodo
        RemoveStrategy[] strategies = {
            new MultiNodeRemoveStrategy(), // Para anillos con múltiples nodos
            new SingleNodeRemoveStrategy() // Para anillos con un solo nodo
        };

        // Determinar el índice: 0 para múltiples nodos, 1 para un solo nodo
        int index = context.next == context ? 1 : 0;
        return strategies[index];
    }

    // Interfaz para estrategias de eliminación
    private interface RemoveStrategy {
        Ring execute(Ring context); // Ejecutar la operación de eliminación
    }

    // Estrategia para eliminar el único nodo en el anillo
    private class SingleNodeRemoveStrategy implements RemoveStrategy {
        @Override
        public Ring execute(Ring context) {
            context.cargo = null; // Limpiar los datos
            context.state = new EmptyRingState(); // Transición al estado vacío
            return context;
        }
    }

    // Estrategia para eliminar un nodo cuando hay múltiples nodos en el anillo
    private class MultiNodeRemoveStrategy implements RemoveStrategy {
        @Override
        public Ring execute(Ring context) {
            Ring nextRing = context.next; // Obtener el siguiente nodo

            // Copiar los datos del siguiente nodo al nodo actual
            context.cargo = nextRing.cargo;
            context.next = nextRing.next; // Saltar el siguiente nodo

            return context;
        }
    }
}