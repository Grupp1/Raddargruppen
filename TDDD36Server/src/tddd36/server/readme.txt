File: readme.txt

[2011-10-26] B�rje:
Detta paktet inneh�ller serverns mest grundl�ggande filer. En ClientHandler skapas varje g�ng
en klient ansluter sig, och varje ClientHandler k�rs i en egen tr�d (implements Runnable). P�
s� vis kan Server.java tilldela varje anslutning en ClientHandler och sedan forts�tta lyssna efter nya
anslutningar. ClientHandler �r den som hanterar anslutningen, l�ser in meddelanden, vidarebefordrar
meddelanden och allt annat som kan beh�va g�ras f�r varje enskild klient. Vi flyttar allts� arbetet fr�n "lyssnartr�den" (Server.java)
till nya "arbetar-tr�dar" (som skapas efterhand anslutningar kommer in). Detta g�r servern multi-tr�dad, och
flera klienter kan s�ledes ansluta sig samtidigt. 



