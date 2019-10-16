$(document).ready(function(){
	/***** MODAL PELANGGAN *****/
	var setDataModalPilihPelanggan = function(){
		let listData = [
          {'nama_pelanggan':'Ricky'},
          {'nama_pelanggan':'Ricky'},
          {'nama_pelanggan':'Ricky'},
          {'nama_pelanggan':'Ricky'},
          {'nama_pelanggan':'Ricky'},
          {'nama_pelanggan':'Ricky'},
          {'nama_pelanggan':'Ricky'},
          {'nama_pelanggan':'Ricky'},
          {'nama_pelanggan':'Ricky'}
		];
		let listDataHTML = "<ul class=\"list-group rounded-0\">";
		
		for(let i=0; i < listData.length; i++){
			listDataHTML += "<li class=\"list-group-item font-weight-bold\">"+listData[i]['nama_pelanggan']+"</li>";
		}		
		listDataHTML += "</ul>";
		$('#modalpilihpelangganbtnkembali').hide();
		$('#modalpilihpelanggantitle').html('Pelanggan');
		$('#modalpilihpelangganbody').html(listDataHTML);
		$('#buttonModalTambahPelanggan').html('Tambah Pelanggan +');//text btn

	}
	//// RUNNING DEFAULT
	setDataModalPilihPelanggan();

	$('#buttonModalTambahPelanggan').click(function(){
		$('#modalpilihpelangganbtnkembali').show(); //arrow back
		$('#modalpilihpelanggantitle').html('Tambah Pelanggan'); //title
		$(this).html('Simpan');//text btn
		$('#modalpilihpelangganbody').html('<div class="row mx-2 my-4">'+
			'<div class="col-12 mb-2">'+
                '<input class="form-control rounded-sm" type="text" placeholder="Nama">'+
              '</div>'+
              '<div class="col-6">'+
                '<input class="form-control rounded-sm" type="text" placeholder="Nomor Hp (Optional)">'+
              '</div>'+
              '<div class="col-6">'+
                '<input class="form-control rounded-sm" type="text" placeholder="Email (Optional)">'+
              '</div>'+
            '</div>');
	});
	$('#modalpilihpelangganbtnkembali').click(function(){
		setDataModalPilihPelanggan();
	});

	/****** MODAL PILIH MEJA ******/

	$('#btnmodalpilihmeja').click(function(){
		$('#modalpilihmeja').modal('show');
	})
});