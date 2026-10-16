    package br.com.dio.lib_padroes_projeto_spring.controller;

    import br.com.dio.lib_padroes_projeto_spring.model.Client;
    import br.com.dio.lib_padroes_projeto_spring.service.ClientService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("clients")
    public class ClientRestController {

        @Autowired
        private ClientService clientService;

        @GetMapping
        public ResponseEntity<Iterable<Client>> searchAll(){
            return ResponseEntity.ok(clientService.searchAll());
        }

        @GetMapping("/{id}")
        public ResponseEntity<Client> searchById(@PathVariable Long id){
            return ResponseEntity.ok(clientService.searchById(id));
        }

        @PostMapping
        public ResponseEntity<Client> insert(@RequestBody Client c){
            clientService.insert(c);
            return ResponseEntity.ok(c);
        }

        @PutMapping("/{id}")
        public ResponseEntity<Client> update(@PathVariable Long id, @RequestBody Client c) {
            clientService.update(c, id);
            return ResponseEntity.ok(c);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id){
            clientService.delete(id);
            return ResponseEntity.noContent().build();
        }
    }
