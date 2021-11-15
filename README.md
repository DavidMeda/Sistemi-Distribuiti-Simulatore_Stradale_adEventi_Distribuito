# Progetto Sistemi Distribuiti (Java RMI):
SIMULATORE DI UNA RETE STRADALE CON 
CONTROLLORI DEL TRAFFICO E SERVER STATISTICHE REMOTO


## Intro

Il sistema ideato simula una rete stradale, composta da strade e incroci, su cui
si muovono i veicoli che compongono il traffico. La rete stradale è stata
modellata mediante un grafo in cui i nodi rappresentano le intersezioni delle
strade mentre gli archi rappresentano le strade. All’interno di questa rete
circolano i veicoli che si spostano da un nodo A a un nodo B , senza però sapere
quale strada percorrere per giungere a destinazione. Ad ogni nodo della rete
viene assegnato un controllore di traffico chiamato RSU (Road Side Unit) in
grado di comunicare con le auto, calcolare le rotte di navigazione, indirizzare
le auto sui percorsi verso le proprie destinazioni, gestire i semafori agli incroci
e raccogliere statistiche di traffico e delle rete. Infine, un server remoto è stato
impiegato per la raccolta di dati statistici, ogni RSU periodicamente invia i dati
delle variabili osservate; alla fine della simulazione vengono calcolate le
statistiche generali dell’intera rete e informati tutti gli RSU dei risultati.
La rete di nodi creata si basa sui principi caratteristici dei sistemi distribuiti: ogni
RSU è modellato come un thread e il cui insieme va a comporre un pool di
risorse in grado di servire le richieste dei veicoli, collaborare tra loro mediante
lo scambio di messaggi, operare in modo concorrente, gestire
autonomamente le risorse senza l’ausilio di un controllore centralizzato,
prendere decisioni sulla base di informazioni locali o mediante la
comunicazione con gli altri elementi del sistema. Per quanto riguarda l’accesso
al server remoto è stato impiegato il protocollo di Java RMI rendendo
disponibile i servizi di statistiche ai client, rappresentati da ogni RSU; grazie
all’interfaccia remota possono richiamare i servizi messi a disposizione e inviare
i dati che in seguito il server gestirà, infine mediante meccanismo di “callback”
verranno informati tutti gli RSU dei risultati finali della rete.
Il vantaggio che il sistema creato fornisce riguarda la possibilità di analisi del
comportamento dei veicoli agli incroci, la sperimentazione di diverse politiche
semaforiche, il monitoraggio del traffico cittadino, infatti è possibile ricreare
una topologia reale di una rete stradale, che sia un quartiere o una città, seguire
l’andamento dei flussi veicolari e infine, effettuando una analisi sui dati raccolti.
## Run

Main class in src/simulazioni/Simulazione.java
