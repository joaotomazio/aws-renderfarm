
1. Generate my SSH key

	```
	ssh-keygen -t rsa
	```

2. Copy public key

	```
	cat ~/.ssh/id_rsa.pub
	```

3. Add key to AWS

	* Network & Security => KeyPairs => Import KeyPair

		Key pair name: `My KeyPair`
		
		Public key contents: `...`
