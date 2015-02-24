walmart-logistics
=================

## Sumário

O serviço LogistcsWS é um web service SOAP que expõe 3 métodos, de acordo com o desafio proposto.

* **public Status addMap(String id, String map) throws IllegalMapException, IllegalArgumentException;**

 Adiciona na base de grafos os mapas que serão depois pesquisados

 *Parametros:*

 - String id: o nome único do mapa. Ex: "SP"
 - String map: string contendo a representação de um ponto no mapa, uma conexão e a distancia entre os pontos **por linha**.

 Exemplo:

 ```
A B 10
B D 15
A C 20
C D 30
B E 50
D E 30
```

 *Retorno*

 Retorna um ENUM definindo o status da requisição, ou exceções no caso de erro

* **public ShortestPathTO queryRoute(String mapName, String start, String destination, float autonomy, float ltPrice) throws IllegalNodesException;**

Consulta o menor caminho entre os pontos, e o custo da viagem.

*Parametros:*

 - String mapName: nome do mapa inserido com o método addMap. Ex: "SP"
 - String start: id do ponto de origem. Ex: "A"
 - String destination: id do ponto de destino. Ex: "D"
 - float autonomy: autonomia do veículo
 - float ltPrice: valor do litro de combustível

 *Retorno:*

Objeto ShortestPathTO que contém o método ```getPath()``` que retona um ```List<String>``` ordenado onde cada item é o nome do ponto no mapa que representa a próxima etapa da viagem e o
método ```calculateTripCost()``` que retorna um float com o valor da jornada, baseado na distancia do menor caminho encontrado, autonomia do veículo e custo do combustível.

* **public String queryRouteStr(String mapName, String start, String destination, float autonomy, float ltPrice) throws IllegalNodesException;**

Mesmo que o acima, mas retorna uma String simplificada, com o nome dos pontos do mapa contendo o menor caminho separados por espaço e o último item é o valor da viagem. Por exemplo,
se usarmos o mapa descrito no exemplo do método addMap e consultarmos por menor caminho entre origem ```A``` e destino ```D```, com autonomia 10 e custo por litro 2.5 a resspota será

```
A B D 6.25
```

### Arquitetura

*SOAP vs REST: foi escolhido para esse projeto uma abordagem com SOAP ao invés REST simplesmente pelo
tempo disponível para desenvolvimento.* ***Pessoalmente,*** *eu considero mais rápido de começar o projeto, embora para este
serviço REST talvez fosse a melhor alternativa. Extender o projeto atual para suportar também uma interface RESTful
nesse ponto seria fácil.*

O projeto usa Spring 4 para implementação web e DI, Apache CXF para os serviços SOAP e Spring Data Neo4J para a
camada de persistência. Gerenciamento de deppendências e build é feito com Maven 3.

Para a persistência dos mapas, um requisito importante era que a base fosse Embedded para facilitar a avaliação. Dessa
forma, o [Neo4J](http://neo4j.com), uma base de grafos que suporta deployments embedded foi uma escolha natural, já que
não existia requisito da base ser relacional. Com esse approach, se o projeto for migrado para produção, o Neo4J
embarcado pode facilmente ser substituído por uma instalacão standalone ou cluster bastando alterar o arquivo
persistence.xml, garantindo assim possibilidade de escalar a plataforma.

*Uma nota sobre o código fonte:* o projeto foi escrito 100% em inglês, desde nome de variáveis até documentação e
comentários. Essa é uma prática pessoal que eu costumo manter para garantir que o código seja legível para pessoas
que não dominem português.


## Build

O projeto requere JDK 1.7+ e Apache MAVEN 3+.
Para criar o build, rodar da raiz do projeto:

 ```
 mvn clean package
 ```

Será criado no diretório target o arquivo **walmartLogistics-1.0-SNAPSHOT.war**

### Testes
Durante o build serão executados testes unitários. É criado no diretório target uma versão da base de dados usada nos testes e
os testes com o webservice criam um server interno com transporte do tipo local, para não depender do ambiente. O cliente
WS tb é criado no ambiente de testes e não interage com nehum serviço exerno ao processo de testes.

## Execução

### Standalone / teste

O projeto vem preparado com um container **Jetty** embutido no build. Para startar o WebService basta executar da raiz do projeto:

```
    mvn jetty:run
```

Acessar o sumário do serviço em localhost:8080/
Ver a seção Integração Contínua no final deste documento para exemplos de como acessar o WSDL.

### Servidor de aplicação

Depois de executar o build com ```mvn clean package```, copiar a o arquivo WAR p/ a pasta webapps (ou equivalente) do
container desejado. O projeto foi testado com Tomcat 7.

### Configuração Importante:

* Arquivo **db.properties** na pasta **main/resource** indica o caminho onde será criada a base de dados. No *linux é preciso garantir que
o diretório configurado possua permissão de escrita p/ o usuário executando o processo*. O db.properties na pasta test/resource
aponta para o target/ portato quaisquer rsquicios da base de testes é eliminada no mvn clean.


## Integração Contínua

Na AWS esta uma instância EC2 (t3-small) rodando Jenkins com a CI do projeto, e também servindo o WebService
em uma instacia Tomcat.

### Dados de acesso Jenkins:

Jenkins: [http://52.10.60.30:8090](http://52.10.60.30:8080)
```
user: walmart
pass: giani
```

### Live Demo WebService

Tomcat CXF SOAP service summary: [http://52.10.60.30:8080](http://52.10.60.30:8080)

Endpoint: [http://52.10.60.30:8080/LogisticsWS](http://52.10.60.30:8080/LogisticsWS)

WSDL: [http://52.10.60.30:8080/LogisticsWS?wsdl](http://52.10.60.30:8080/LogisticsWS?wsdl)

