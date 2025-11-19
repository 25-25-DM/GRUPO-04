let json = {
    nombre: "",
    apellido: "",
    cedula: ""
}

function obtener() {
    json.nombre = document.getElementById('id_nombre').value = "Cristian";
    json.apellido = document.getElementById('id_apellido').value = "Lechon";
    json.cedula = document.getElementById('id_cedula').value = "1726696064";
}


function guardar(){
    document.getElementById('id_json').value = JSON.stringify(json);
    console.log("el json", json);
}

function borrar(){
    document.getElementById('id_json').value = "";
}