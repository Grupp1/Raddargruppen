File: readme.txt

[2011-10-26] Börje:
Detta paktet innehåller serverns mest grundläggande filer. En ClientHandler skapas varje gång
en klient ansluter sig, och varje ClientHandler körs i en egen tråd (implements Runnable). På
så vis kan Server.java tilldela varje anslutning en ClientHandler och sedan fortsätta lyssna efter nya
anslutningar. ClientHandler är den som hanterar anslutningen, läser in meddelanden, vidarebefordrar
meddelanden och allt annat som kan behöva göras för varje enskild klient. Vi flyttar alltså arbetet från "lyssnartråden" (Server.java)
till nya "arbetar-trådar" (som skapas efterhand anslutningar kommer in). Detta gör servern multi-trådad, och
flera klienter kan således ansluta sig samtidigt. 



