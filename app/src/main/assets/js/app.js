var login = function(){
  $.ajax({
    url: 'http://api.kapcake.com/public/api/kasir/login',
    method: 'POST',
    // header:{
    //   'Content-Type' : 'application/json'
    // },
    data: {
      email: "rra.rickyresky@gmail.com",
      password: "testes"
    },
    success: function(data){
          setStorage(data);
          if(getDataUser().jumlah_akses_outlet  === 1)
             setDataAwal();
        },
        statusCode :{
          204: function(res){
            alert('err');
          }
        }
      });
}
login();
             // setDataAwal();

var sidebarToggleOpacity = false;

$('#sidebarToggle , #sidebarToggleOpacity').click(function(){

  if(sidebarToggleOpacity === false)

    $('#sidebarToggleOpacity').css('display','block');

  else

    $('#sidebarToggleOpacity').css('display','none');

  sidebarToggleOpacity = !sidebarToggleOpacity;

});
$('#kasir').show();
$('#pesanan').hide();
$('#riwayat').hide();
$('#pengaturan').hide();
///// PROSES PEMBUATAN STORAGE ///////
function setStorage(res){
 /**
 * Storage user menyimpan data user yang telah login
 * dan digunakan dalam menu profil serta mengikutsertakan data khusus id user
 * pada fungsi auth digunakan untuk menentukan level usernya 
 */
 let user = res.user;
 let dataUser = {
   id : user.id,
   nama: user.nama,
   email: user.email,
   telpon: user.telpon,
   alamat: user.alamat,
   is_super_admmin: user.is_super_admmin,
   bisnis_id: user.bisnis_id,
   jumlah_akses_outlet: user.outlet.length,
 };
 localStorage.setItem('user', JSON.stringify(dataUser));

     /**
     * Storage Outlet untuk menyimpan outlet yang dapat diakses oleh user
     */
     let outlet = user.outlet;
     localStorage.setItem('outlet', JSON.stringify(outlet));


     /**
     * Storage no_urut_pesanan untuk menyimpan nomor urut pesanan
     */
     localStorage.setItem('no_urut_pesanan', JSON.stringify(1));

     /**
     * Storage pesanan untuk menyimpan pesanan sementara sebelum dibayar
     */
     localStorage.setItem('pesanan', JSON.stringify([]));

     /**
     * Storage riwayat untuk menyimpan riwayat pesanan
     */
     localStorage.setItem('riwayat', JSON.stringify([]));

     /**
     * Storage access dikhususkan untuk autentikasi dan akses pin saja
     */
     let access = {
   is_logged_in : 1,  /// default sementara aktif
   is_pinned_access: 1, /// default sementara aktif
   pin: user.pin, /// default pin 1111
 };
 localStorage.setItem('access', JSON.stringify(access));

     /**
     * Storage setting menyimpan data penyetingan di menu setting
     * settingan ini terkhusus settingan umum di menu setting > menu
     */
     let setting = {
   is_stock_tracking: 1, /// settingan track menu stok
 };
 localStorage.setItem('setting', JSON.stringify(setting));

     /**
     *  Storage data menyimpan data lain yang akan digunakan dalam aplikasi
     *  dan menyimpan data variable apabila aplikasi closed 
     */
     let dataSet = { 
       no_pemesanan: 0,
       outlet_terpilih_id : 24,
       kategori_menu_terpilih : 0 
     }
     localStorage.setItem('data', JSON.stringify(dataSet));

     /**
     *  Storage token dikhususkan untuk menyimpan token akses data API
     *  Storage token akan dikosongkan apabila user telah logout
     */
     localStorage.setItem('token', res.token);

     /**
     * Storage pesanan terfokus berfungsi sebagai penyimpanan sementara data pesanan yang barusaja dipesan atau pesanan yang baru saja dilanjutkan
     */
     resetStoragePesananTerfokus();
   }

   function getToken(){
     return localStorage.getItem('token');

   }

   function setOutletTerpilih(id){
     var user = JSON.parse(localStorage.getItem('user'));
     user.outlet_terpilih_id = id;
     localStorage.setItem('user', JSON.stringify(user));
   }

   function getOutletTerpilih(){
     let data = JSON.parse(localStorage.getItem('data'));
     return data.outlet_terpilih_id;
   }

var menuOpsi = []; //variable ini untuk simpan data menu dari hasi get setdata awal agar tidak perlu load lagi dari storage

function setDataAwal(){
    var outlet_terpilih_id = JSON.parse(localStorage.getItem('data')).outlet_terpilih_id;  // ini sudah benar hanya perlu ditambahkan  di anroid save outlet terpilih
    $.ajax({
     method: 'GET',
     url: 'https://api.kapcake.com/public/api/kasir/outlet',
     headers: {
       'Accept':'application/json',
       'Content-Type':'application/json',
       'Authorization':'Bearer '+getToken(),
     },
     dataType: 'json',
     data: {
       outlet_id : outlet_terpilih_id
     },
     success: async function(data){
       localStorage.setItem('menu', JSON.stringify(data.menu));
       localStorage.setItem('kategori_menu', JSON.stringify(data.kategori_menu));
       await setHTMLKategoriMenu();
       await setHTMLMenu();
       menuOpsi = data.menu;
       setDefaultPesanan();
     $('#kasir').show();      // -<<<<<<<     ini masih dipake yahhh

         //proses ini bisa setelahnya set data
         localStorage.setItem('jenis_pemesanan', JSON.stringify(data.jenis_pemesanan));
         localStorage.setItem('meja', JSON.stringify(data.kategori_meja));
         localStorage.setItem('diskon', JSON.stringify(data.diskon));
         localStorage.setItem('biaya_tambahan', JSON.stringify(data.biaya_tambahan));
         localStorage.setItem('pajak', JSON.stringify(data.pajak));
         localStorage.setItem('pelanggan', JSON.stringify(data.pelanggan));

         let dataPemesanan = setDataPemesananStorageFormat(data.pemesanan);
       localStorage.setItem('pesanan', JSON.stringify(dataPemesanan));

       localStorage.setItem('riwayat', JSON.stringify(data.penjualan));

       localStorage.setItem('no_urut_pesanan', JSON.stringify(data.no_urut_pesanan));

       setHTMLMeja();
       setHTMLDiskon();
       setHTMLBiayaTambahan();
       setHTMLPelanggan();
     setHTMLRiwayat();
     }
 });
 
}
// /////// /////// /////// /////// PAGE PAGE PAGE PAGE PAGE PAGE PAGE PAGE PAGE  /////////////////////////////////////
async function page(menu){
 var menuList = ['kasir', 'pesanan', 'riwayat', 'pengaturan'];
 if(menu === 'kasir'){
   await setReOpenPesananTerfokus();

 }else if(menu == 'pesanan'){
   setHTMLPesananTersimpan();
 }


 for(let i = 0; i < menuList.length; i++){
   if(menuList[i] === menu) $('#'+menu).show();
   else $('#'+menuList[i]).hide();
 }
}
//////////***************?????????????????????????  PROSES HALAMAN KASIR  ??????????????????????**************//////////
var kategoriMenuTerpilih = 0;
var mejaTerpilih = 0;
var menuTerpilih = 0;
var variasiTerpilih = 0;
var jenisPemesananTerpilih = 0;
var catatanTerpilih = '';
var jumlahTerpilih = 1;
var pelangganTerpilih = 0;
var diskonTerpilih = 0;
var biayaTambahanTerpilih = 0;

function getDataUser(){
 return JSON.parse(localStorage.getItem('user'));
}

function getPesananTerfokus(){
 return JSON.parse(localStorage.getItem('pesanan_terfokus'));
}

/*********** KATEGORI MENU *************/
function setHTMLKategoriMenu(){
 let data = JSON.parse(localStorage.getItem('kategori_menu'));
 var htmlList = '<li  class="mui--is-active opsi-kategori-menu" data-index="0">'+
 '<a>Semua</a>'+
 '</li>'; 
 for (var i = 0; i < data.length; i++) {
   htmlList = htmlList + '<li class="mui opsi-kategori-menu" data-index="'+data[i].id_kategori_menu+'">'+
   '<a>'+data[i].nama_kategori_menu+'</a>'+
   '</li>';
 }
 $('#listKategoriMenu').html(htmlList);
}

function setStorageKategoriMenuTerpilih(){
 let data = JSON.parse(localStorage.getItem('data'));
 data.kategori_menu_terpilih = kategoriMenuTerpilih;
 localStorage.setItem('data', JSON.stringify(data));
}

$('body').on('click','.opsi-kategori-menu',function(){
 kategoriMenuTerpilih = parseInt($(this).attr('data-index'));
 $('.opsi-kategori-menu').removeClass("mui--is-active");
 $(this).addClass("mui--is-active");
 // kategori_menu_id
 let menu = JSON.parse(localStorage.getItem('menu'));

 if(kategoriMenuTerpilih !== 0)
   menu = menu.filter(value => value.kategori_menu_id === kategoriMenuTerpilih);
 setHTMLMenu(menu, true);
 setStorageKategoriMenuTerpilih(); //hanya proses simpan 
});



/*********** MENU *************/
function setHTMLMenu(menu = [], filtered = false){
 if(filtered == false)
   menu = JSON.parse(localStorage.getItem('menu'));

 var htmlList ="";
 for (var i = 0; i < menu.length; i++) {
   htmlList = htmlList + '<div class="px-1  col-lg-3 col-md-4 col-sm-6">'+
   '<div class="card mb-3 border-0 opsi-menu" data-index="'+menu[i]['id_menu']+'">'+
   '<img src="'+(menu[i]['gambar'].length > 0 ? menu[i]['gambar'][1]['path']:'')+'" class="card-img-top" alt="'+menu[i]['nama_menu']+'">'+
   '<div class="card-body px-2 py-1">'+
   '<label class="m-0"><b>'+menu[i]['nama_menu']+'</b></label>'+
   '<p class="card-text"><small class="text-muted">'+ (menu[i]['variasi'].length > 1 ? menu[i]['variasi'].length+' Variasi' : menu[i]['variasi'][0]['harga'])  +'</small></p>'+
   '</div>'+
   '</div>'+
   '</div>'; 
 }
 $('#listMenu').html(htmlList);
}

/*********** PENCARIAN MENU **************/
$('body').on('keyup','#pencarianMenu',function(){
 let text = $(this).val();
 console.log(text.length);
 let menu = [];
 if(text !== '' && text !== null && text.length >= 2 ){
   menu = menuOpsi.filter(item => (item.nama_menu.toLowerCase()).indexOf(text) > -1);
   setHTMLMenu(menu, true);
 }
});

/***********MODAL PEMESANAN**************/
// modalpemesanan
$('body').on('click','.opsi-menu',function(){
 menuTerpilih = parseInt($(this).attr('data-index'));

 let data = JSON.parse(localStorage.getItem('menu'));
 var menu = data.find(d => parseInt(d.id_menu) === parseInt(menuTerpilih) );

 $('#modalpemesanantitle').html(menu.nama_menu);

 /// JENIS PEMESANAN
 let tipePenjualan = menu.tipe_penjualan;
 $('#modalpemesananjenispemesananhr').hide();
 $('#modalpemesananjenispemesanantitle').hide();
 $('#modalpemesananjenispemesanan').hide();

 if(tipePenjualan.length > 1 ){
   var htmlListTipePenjualan = '';
   for(var a = 0; a < tipePenjualan.length; a++){
     htmlListTipePenjualan = htmlListTipePenjualan + 
     '<div class="col-6 mb-2">'+
     '<div class="d-flex rounded border border-primary p-3 opsi-menu-jenis-pemesanan" data-index="'+tipePenjualan[a]['tipe_penjualan_id']+'">'+
     '<div class="text-left">'+tipePenjualan[a]['tipe_penjualan']['nama_tipe_penjualan']+'</div>'+
     '</div>'+
     '</div>';
   }
   $('#modalpemesananjenispemesananhr').show();
   $('#modalpemesananjenispemesanantitle').show();
   $('#modalpemesananjenispemesanan').show();
   $('#modalpemesananjenispemesanan').html(htmlListTipePenjualan);

 }else{
   jenisPemesananTerpilih = tipePenjualan[0]['tipe_penjualan_id'];
 }

 /// VARIASI MENU
 $('#modalpemesananvariasihr').hide();
 $('#modalpemesananvariasititle').hide();
 $('#modalpemesananvariasi').hide();
 var htmlListVariasi = '';
 let variasi = menu.variasi;
 if(variasi.length > 1){
   $('#modalpemesananvariasihr').show();
   $('#modalpemesananvariasititle').show();
   $('#modalpemesananvariasi').show();
   for(var a = 0; a < variasi.length; a++){
     htmlListVariasi = htmlListVariasi + 
     '<div class="col-6 mb-2">'+
     '<div class="d-flex rounded border border-primary opsi-menu-variasi p-3" data-index="'+variasi[a]['id_variasi_menu']+'">'+
     '<div class="text-left">'+variasi[a]['nama_variasi_menu']+'</div>'+
     '</div>'+
     '</div>';
   }
   $('#modalpemesananvariasi').html(htmlListVariasi);
 }else{
   variasiTerpilih = variasi[0]['id_variasi_menu'];
 }

 $('#opsi-menu-jumlah').val('1');
 $('#opsi-menu-btn-hapus').hide();
 $('#opsi-menu-btn-simpan').html('Tambahkan');
 $('#opsi-menu-btn-simpan').removeClass('w-50');
 $('#opsi-menu-btn-simpan').addClass('w-100');

 $('#modalpemesanan').modal('show');
});

$('body').on('click','.opsi-menu-jenis-pemesanan',function(){
 jenisPemesananTerpilih = parseInt($(this).attr('data-index'));
 $('.opsi-menu-jenis-pemesanan').removeClass(" bg-primary text-white");
 $(this).addClass("bg-primary text-white");
});

$('body').on('click','.opsi-menu-variasi',function(){
 variasiTerpilih = parseInt($(this).attr('data-index'));
 $('.opsi-menu-variasi').removeClass(" bg-primary text-white");
 $(this).addClass("bg-primary text-white");
});

$('body').on('click','#opsi-menu-minus',function(){
 jumlahTerpilih = parseInt($('#opsi-menu-jumlah').val());
 if(jumlahTerpilih > 1)
   jumlahTerpilih = jumlahTerpilih - 1;

 $('#opsi-menu-jumlah').val(jumlahTerpilih);
});

$('body').on('click','#opsi-menu-plus',function(){
 jumlahTerpilih = parseInt($('#opsi-menu-jumlah').val());
 // if(jumlahTerpilih > 1)
 jumlahTerpilih = jumlahTerpilih + 1;

 $('#opsi-menu-jumlah').val(jumlahTerpilih);
});

$('body').on('change','#opsi-menu-catatan',function(){
 catatanTerpilih = $('#opsi-menu-catatan').val();
});

$('body').on('click','#opsi-menu-btn-simpan', async function(){
 if(variasiTerpilih === 0 || jenisPemesananTerpilih === 0) return;

 let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));

 if(data.pesanan.length === 0 ){
   let nomorPesanan = setNomorPesanan(data, true);
   data.is_uploaded = 0;
   data.no_pemesanan = nomorPesanan['no_pemesanan']; 
   data.kode_pemesanan = nomorPesanan['kode_pemesanan']; 
   $('#kode_pemesanan').html('#'+data.kode_pemesanan);
   if(data.pajak_id === 0 ){
     let dataPajak = JSON.parse(localStorage.getItem('pajak'));
     data.pajak_id = dataPajak.id_pajak;
     data.nama_pajak = dataPajak.nama_pajak;
     data.jumlah_pajak = dataPajak.jumlah;
   }
 }

 let menus = JSON.parse(localStorage.getItem('menu'));

 let menu = menus.find(m => m.id_menu === menuTerpilih);

 let tipePenjualan = (menu.tipe_penjualan.find(t => parseInt(t.tipe_penjualan_id) === parseInt(jenisPemesananTerpilih) ) ).tipe_penjualan;

 let harga = 0;
 let variasi = menu.variasi.find(v => v.id_variasi_menu === variasiTerpilih);
 if(variasi.tipe_penjualan.length > 0)
   harga = (variasi.tipe_penjualan.find(h => h.tipe_penjualan_id === tipePenjualan.id_tipe_penjualan)).harga;
 else
   harga = variasi.harga;

 let menuData = {
   'menu_id' : menu.id_menu,
   'nama_menu' : menu.nama_menu,
   'variasi_menu_id' : variasi.id_variasi_menu,
   'nama_variasi_menu' : menu.variasi.length > 1 ? variasi.nama_variasi_menu : '',
   'tipe_penjualan_id' : tipePenjualan.id_tipe_penjualan,
   'nama_tipe_penjualan' : tipePenjualan.nama_tipe_penjualan,
   'jumlah' : jumlahTerpilih,
   'harga' : harga,
   'total' : parseInt(jumlahTerpilih) * parseInt(harga),
 };
 if( data.pesanan.length < 1 ){
   await setBlankListPesanan();
 }

 var jmlMenu = 0;
 var jmlTipePenjualan = 0;
 $.each( data.pesanan, function( key, m ) {
   $.each( m.menu, function( k, j ) {
     if( 
       (j.menu_id === menuData.menu_id) && 
       (j.variasi_menu_id === menuData.variasi_menu_id) && 
       (j.tipe_penjualan_id === menuData.tipe_penjualan_id)
       ){
       data.pesanan[key].menu[k]['jumlah'] = data.pesanan[key].menu[k]['jumlah'] + menuData.jumlah; 
     data.pesanan[key].menu[k]['total'] = data.pesanan[key].menu[k]['jumlah'] * data.pesanan[key].menu[k]['harga']; 

     jmlMenu++;
   }
 });

   if(m.id_tipe_penjualan === tipePenjualan.id_tipe_penjualan){
     jmlTipePenjualan++;
   }
 });

 if(jmlTipePenjualan === 0 ){
   data.pesanan.push({
     'id_tipe_penjualan' : tipePenjualan.id_tipe_penjualan,
     'nama_tipe_penjualan' : tipePenjualan.nama_tipe_penjualan,
   });
 }

 let indexJenisPemesanan = data.pesanan.findIndex( j => j.id_tipe_penjualan === menuData.tipe_penjualan_id);
 if(data.pesanan[indexJenisPemesanan].menu === undefined)
   data.pesanan[indexJenisPemesanan].menu = [];

 if(jmlMenu === 0)
   data.pesanan[indexJenisPemesanan].menu.push(menuData);


 setListPesanan(data.pesanan);
 await localStorage.setItem('pesanan_terfokus', JSON.stringify(data));

 setKalkulasi();

 $('#modalpemesanan').modal('hide');

 menuTerpilih = 0;
 variasiTerpilih = 0;
 jenisPemesananTerpilih = 0;
 catatanTerpilih = '';
 jumlahTerpilih = 1;
});

function setNomorPesanan(data=[], returnData = false){
 let noUrutPesanan = JSON.parse(localStorage.getItem('no_urut_pesanan'));
 let user = JSON.parse(localStorage.getItem('user'));
 let tanggal = datetimeEvent();
 kodePesanan = noUrutPesanan + '-' + user.id + '-' + tanggal['tanggal'];

 if(returnData === false){
   let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));
   data.no_pemesanan = parseInt(noUrutPesanan);
   data.kode_pemesanan = kodePesanan;
 }

 noUrutPesanan = parseInt(noUrutPesanan) + 1;
 localStorage.setItem('no_urut_pesanan', JSON.stringify(noUrutPesanan));

 if(returnData === false){
   localStorage.setItem('pesanan_terfokus', JSON.stringify(data));
 }else{
   return {
     'no_pemesanan' : noUrutPesanan,
     'kode_pemesanan' : kodePesanan,
   };
 }
}

// daftar pesanan
function resetHTMLListPesanan(){
 $('#list-pesanan').html('<li class="list-group-item py-1 text-center">Tidak Ada Pesanan</li>');
}

function setBlankListPesanan(){
 $('#list-pesanan').html('');
}

function setListPesanan(pesanan = []){
 if(pesanan.length <= 0 )
   pesanan = (JSON.parse(localStorage.getItem('pesanan_terfokus'))).pesanan;

 let htmlListPesanan = '';
 $.each(pesanan, function( key, m ) {
   htmlListPesanan = htmlListPesanan + '<li class="list-group-item py-1 text-center" data-index="'+ m.id_tipe_penjualan +'">'+ m.nama_tipe_penjualan +'</li>';
   $.each( m.menu, function( k, menu) {
     htmlListPesanan = htmlListPesanan + '<li class="list-group-item px-3 py-1 list-pesanan-menu" data-index="'+ menu.menu_id +'">'+
     '<div class="d-flex">'+
     '<div class="pr-1 flex-grow-1 text-left" style="width: 50%">'+
     '<div class="text-truncate font-weight-bold">'+menu.nama_menu+'</div>'+
     (menu.nama_variasi_menu !== '' ?  '<span class="text-muted">'+menu.nama_variasi_menu+'</span><br/>' : '')+
     '</div>'+
     '<div class="bd-highlight text-center" style="width: 20%">X&nbsp;'+menu.jumlah+'</div>'+
     '<div class="bd-highlight text-right" style="width: 30%">Rp'+menu.total+'</div>'+
     '</div>'+
     '</li> ';
   });
 });

 $('#list-pesanan').html(htmlListPesanan);
}

function setKalkulasi(){
 let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));
 let pajak = JSON.parse(localStorage.getItem('pajak'));

 data.subtotal = 0;
 data.total_item = 0;
 $.each( data.pesanan, function( key, p ) {
   $.each( p.menu, function( k, m ) {
     data.subtotal = data.subtotal + parseInt(m.total);
     data.total_item = data.total_item + parseInt(m.jumlah);
   });
 });


 /// diskon
 if(data.tipe_diskon === "persen"){
   data.total_diskon = (data.subtotal * parseFloat(data.jumlah_diskon)) / 100;
 }else if(data.tipe_diskon === "rupiah"){
   data.total_diskon = data.jumlah_diskon;
 }
 data.total = data.subtotal - data.total_diskon;

 /// biaya tambahan
 data.total_biaya_tambahan = (data.total*data.jumlah_biaya_tambahan) / 100 ,
 data.total = data.total + data.total_biaya_tambahan;

 /// pajak
 data.pajak_id = pajak.id_pajak;
 data.nama_pajak = pajak.nama_pajak;
 data.jumlah_pajak = pajak.jumlah;
 data.total_pajak = (data.total * pajak.jumlah) / 100;

 data.total = data.total + data.total_pajak;
 localStorage.setItem('pesanan_terfokus', JSON.stringify(data));

 $('#pesananSubtotalText').html(data.subtotal);
 $('#pesananDiskonText').html(data.total_diskon);
 $('#pesananBiayaTambahanText').html(data.total_biaya_tambahan);
 $('#pesananPajakText').html(data.total_pajak);
 $('#pesananTotalBayarText').html(data.total);
}
function resetHTMLKalkulasiPesanan(){
 $('#namaPelanggan').html('Pelanggan');
 $('#namaMeja').html('Meja');
 $('#pesananSubtotalText').html(0);
 $('#pesananDiskonText').html(0);
 $('#pesananBiayaTambahanText').html(0);
 $('#pesananPajakText').html(0);
 $('#pesananTotalBayarText').html(0);
}

function resetStoragePesananTerfokus(){
 let pesananTerfokusSet = { 

   tanggal_simpan: false,
   waktu_simpan: false,

   tanggal_proses: false,
   waktu_proses: false,

   no_pemesanan: 0,
   kode_pemesanan: '',

   pesanan : [],
   total_item : 0,

   pelanggan_id : 0,
   nama_pelanggan : '',

   kategori_meja_id : 0, 
   nama_kategori_meja : '', 
   meja_id : 0,
   nama_meja : '',

   diskon_id : 0,
   nama_diskon : '',
   jumlah_diskon : 0,
   total_diskon : 0,

   biaya_tambahan_id : 0,
   nama_biaya_tambahan : '',
   jumlah_biaya_tambahan : 0,
   total_biaya_tambahan : 0,

   pajak_id : 0,
   nama_pajak : '',
   jumlah_pajak : 0,
   total_pajak : 0,

   subtotal : 0,
   total : 0,
   catatan : ''

 }
 localStorage.setItem('pesanan_terfokus', JSON.stringify(pesananTerfokusSet));
}


/********** MEJA *********/
function setHTMLMeja(){
 let data = JSON.parse(localStorage.getItem('meja'));
 var htmlList ="";
 for (var i = 0; i < data.length; i++) {
   htmlList = htmlList + '<li class="list-group-item font-weight-bold border-0 active">'+data[i].nama_kategori_meja+'</li>'+
   '<li class="list-group-item p-0 font-weight-bold">'+
   '<table style="width: 100%">';
   for(var a = 0; a < data[i].meja.length; a++ ){
     if(a % 4 == 0) htmlList = htmlList + '<tr>';
     htmlList = htmlList + '<td class="px-2 py-4 text-center border opsi-meja '+ (data[i].meja[a].id_meja === mejaTerpilih ? 'meja-option-active' : '' )+'" data-index="'+data[i].meja[a].id_meja+'">'+data[i].meja[a].nama_meja+'</td>';
     if(a!=0 && a % 4 == 0) htmlList = htmlList + '</tr>';
   }
   htmlList = htmlList + 
   '</table>'+
   '</li>'; 
 }
 $('#listMeja').html(htmlList);
}

function setStorageMejaTerpilih(){
 let dataMeja = JSON.parse(localStorage.getItem('meja'));
 let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));

 $.each( dataMeja, function( key, l ) {
   $.each( l.meja, function( k, m ) {
     if(mejaTerpilih === m.id_meja){
       data.meja_id = m.id_meja;
       data.nama_meja = m.nama_meja;

       $('#namaMeja').html(m.nama_meja +' - '+ l.nama_kategori_meja);
     }
   });
 });

 localStorage.setItem('pesanan_terfokus', JSON.stringify(data));
}

$('body').on('click','.opsi-meja',function(){
 mejaTerpilih = parseInt($(this).attr('data-index'));
 $('.opsi-meja').removeClass("meja-option-active");
 $(this).addClass("meja-option-active");

 setStorageMejaTerpilih();
});

/*********** PELANGGAN ***********/

function setHTMLPelanggan(){
 let data = JSON.parse(localStorage.getItem('pelanggan'));
 let htmlList = "<ul class=\"list-group rounded-0\">";

 for(let i=0; i < data.length; i++){
   htmlList += "<li class=\"list-group-item font-weight-bold opsi-pelanggan "+(data[i].id_pelanggan === pelangganTerpilih ? 'active' : '' )+" \"  data-index="+data[i]['id_pelanggan']+">"+data[i]['nama_pelanggan']+"</li>";
 }   
 htmlList += "</ul>";
 $('#modalpilihpelangganbtnkembali').hide();
 $('#modalpilihpelanggantitle').html('Pelanggan');

 $('#pencarianModalPelanggan').show();

 $('#buttonModalSimpanPelanggan').hide();
 $('#buttonModalTambahPelanggan').show();

 $('#modalpilihpelangganbody').html(htmlList);
}

$('#btnModalKembaliPelanggan').click(function(){
 setHTMLPelanggan();
});

$('#buttonModalTambahPelanggan').click(function(){
 $(this).hide();
 $('#buttonModalSimpanPelanggan').show();
 $('#pencarianModalPelanggan').hide();
 $('#modalpilihpelangganbtnkembali').show(); //arrow back
 $('#modalpilihpelanggantitle').html('Tambah Pelanggan'); //title
 $('#modalpilihpelangganbody').html('<div class="row mx-2 my-4">'+
   '<div class="col-12 mb-2">'+
   '<input class="form-control rounded-sm" name="nama_pelanggan" type="text" placeholder="Nama">'+
   '</div>'+
   '<div class="col-6">'+
   '<input class="form-control rounded-sm" type="text" name="no_hp_pelanggan" placeholder="Nomor Hp (Optional)">'+
   '</div>'+
   '<div class="col-6">'+
   '<input class="form-control rounded-sm" type="text" name="email_pelanggan" placeholder="Email (Optional)">'+
   '</div>'+
   '</div>');
});

$('#buttonModalSimpanPelanggan').click(function(){
 let nama = $('input[name=nama_pelanggan]').val();
 let no_hp = $('input[name=no_hp_pelanggan]').val();
 let email = $('input[name=email_pelanggan]').val();
 $.ajax({
   url: 'https://api-backoffice.kapcake.com/api/kasir/pelanggan/'+getOutletTerpilih(),
   headers: {
     'Content-Type':'application/json',
     'Accept':'application/json',
     'Authorization':'Bearer '+getToken(),
   },
   method: 'POST',
   data: JSON.stringify({
     nama_pelanggan : $('input[name=nama_pelanggan]').val(),
     email : $('input[name=email_pelanggan]').val(),
     no_hp : $('input[name=no_hp_pelanggan]').val(),
   }),
   success: function(data){
     let pelanggan = JSON.parse(localStorage.getItem('pelanggan'));
     pelanggan.push(data);
     localStorage.setItem('pelanggan', JSON.stringify(pelanggan));
     setHTMLPelanggan();
   }
 });
});


function setStoragePelangganTerpilih(){
 let dataPelanggan = JSON.parse(localStorage.getItem('pelanggan'));
 let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));

 let pelanggan = dataPelanggan.find(p => parseInt(p.id_pelanggan) === parseInt(pelangganTerpilih));

 data.pelanggan_id = pelanggan.id_pelanggan;
 data.nama_pelanggan = pelanggan.nama_pelanggan;

 localStorage.setItem('pesanan_terfokus', JSON.stringify(data));

 $('#namaPelanggan').html(pelanggan.nama_pelanggan);
}

$('body').on('click','.opsi-pelanggan', function(){
 pelangganTerpilih = parseInt($(this).attr('data-index'));

 $('.opsi-pelanggan').removeClass("active");
 $(this).addClass("active");

 setStoragePelangganTerpilih();
 $('#modalpilihpelanggan').modal('hide');
});


/*********** DISKON ***********/

function setHTMLDiskon(){
 let data = JSON.parse(localStorage.getItem('diskon'));
 let htmlList = "<ul class=\"list-group rounded-0\">";

 for(let i=0; i < data.length; i++){
   htmlList += "<li class=\"list-group-item font-weight-bold opsi-diskon "+(data[i].id_diskon === diskonTerpilih ? 'active' : '' )+" \"  data-index="+data[i]['id_diskon']+">"+data[i]['nama_diskon']+"<span class=\"float-right\">"+(data[i]['tipe_diskon'] === 'persen' ? data[i]['jumlah']+'%': 'Rp'+data[i]['jumlah'])+"</span></li>";
 }   
 htmlList += "</ul>";

 $('#modalpilihdiskonbody').html(htmlList);
}


function setStorageDiskonTerpilih(){
 let dataDiskon = JSON.parse(localStorage.getItem('diskon'));
 let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));

 let diskonFiltered = dataDiskon.filter(d => parseInt(d.id_diskon) === parseInt(diskonTerpilih));
 if(diskonFiltered.length > 0){
   let diskon = dataDiskon.find(d => parseInt(d.id_diskon) === parseInt(diskonTerpilih));
   data.diskon_id = diskon.id_diskon;
   data.nama_diskon = diskon.nama_diskon;
   data.tipe_diskon = diskon.tipe_diskon;
   data.jumlah_diskon = diskon.jumlah;
 }

 localStorage.setItem('pesanan_terfokus', JSON.stringify(data));
}

$('body').on('click','.opsi-diskon',function(){
 diskonTerpilih = parseInt($(this).attr('data-index'));
 $('.opsi-diskon').removeClass("active");
 $(this).addClass("active");
});

$('body').on('click','#buttonModalPilihDiskon', async function(){
 await setStorageDiskonTerpilih();
 $('#modalpilihdiskon').modal('hide');
 setKalkulasi();
});

/*************** BIAYA TAMBAHAN ***************/
function setHTMLBiayaTambahan(){
 let data = JSON.parse(localStorage.getItem('biaya_tambahan'));
 let htmlList = "<ul class=\"list-group rounded-0\">";

 for(let i=0; i < data.length; i++){
   htmlList += "<li class=\"list-group-item font-weight-bold opsi-biaya-tambahan "+(data[i].id_biaya_tambahan === biayaTambahanTerpilih ? 'active' : '' )+" \"  data-index="+data[i]['id_biaya_tambahan']+">"+data[i]['nama_biaya_tambahan']+"<span class=\"float-right\">"+data[i]['jumlah']+"%</span></li>";
 }   
 htmlList += "</ul>";

 $('#modalpilihbiayatambahanbody').html(htmlList);
}

function setStorageBiayaTambahanTerpilih(){
 let dataBiayaTambahan = JSON.parse(localStorage.getItem('biaya_tambahan'));
 let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));

 let biayaTambahanFiltered = dataBiayaTambahan.filter(d => parseInt(d.id_biaya_tambahan) === parseInt(biayaTambahanTerpilih));
 if(biayaTambahanFiltered.length > 0){
   let biayaTambahan = dataBiayaTambahan.find(d => parseInt(d.id_biaya_tambahan) === parseInt(biayaTambahanTerpilih));
   data.biaya_tambahan_id = biayaTambahan.id_biaya_tambahan;
   data.nama_biaya_tambahan = biayaTambahan.nama_biaya_tambahan;
   data.jumlah_biaya_tambahan = parseFloat(biayaTambahan.jumlah);
 }

 localStorage.setItem('pesanan_terfokus', JSON.stringify(data));
}

$('body').on('click','.opsi-biaya-tambahan',function(){
 biayaTambahanTerpilih = parseInt($(this).attr('data-index'));
 $('.opsi-biaya-tambahan').removeClass("active");
 $(this).addClass("active");
});

$('body').on('click','#buttonModalPilihBiayaTambahan', async function(){
 await setStorageBiayaTambahanTerpilih();
 $('#modalpilihbiayatambahan').modal('hide');
 setKalkulasi();
});


/************************************ TOMBOL SIMPAN PESANAN ************************/
$('body').on('click','#btnSimpanPesanan',  function(){
 let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));
 let pesanan = JSON.parse(localStorage.getItem('pesanan'));

 let jumlahPesananTerdeteksi = (pesanan.filter(p => p.kode_pemesanan === data.kode_pemesanan)).length;
 if(data.pesanan.length > 0){
   if(jumlahPesananTerdeteksi === 0){
     let dateTime = datetimeEvent();
     data.tanggal_simpan = dateTime.tanggal;
     data.waktu_simpan = dateTime.waktu;
     pesanan.push(data);
   }else if(jumlahPesananTerdeteksi > 0){
     let pesananIndex = pesanan.findIndex(p => p.kode_pemesanan === data.kode_pemesanan);
     data.is_uploaded = 0;
     pesanan[pesananIndex] = data;
   }

   localStorage.setItem('pesanan', JSON.stringify(pesanan));
   setDefaultPesanan();
   sinkronisasiPostDataPesanan();
   $('#modalinformasipesanandisimpan').modal('show');
 }else{
   alert('pesanan tidak ada');
 }
});
$('body').on('click','#opsipilihhapuspesanan',function(){
 $('#modalkonfirmasihapuspesanantersimpan').modal('show');
});




/************************** PEMBAYARAN ******************/
var totalPembayaran = 0;
var nominalPembayaran = 0;
var kembalianPembayaran = -1;
$('body').on('click','#btnBayarPesanan',function(){
 // let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));
 // let pesanan = JSON.parse(localStorage.getItem('pesanan'));

 // let jumlahPesananTerdeteksi = (pesanan.filter(p => p.kode_pemesanan === data.kode_pemesanan)).length;
 // let dateTime = datetimeEvent();
 // data.tanggal_simpan = dateTime.tanggal;
 // data.waktu_simpan = dateTime.waktu;

 // if(data.pesanan.length > 0){
 //  if(jumlahPesananTerdeteksi === 0){
 //    pesanan.push(data);
 //  }else{
 //    let pesananIndex = pesanan.findIndex(p => p.kode_pemesanan === data.kode_pemesanan);
 //    pesanan[pesananIndex] = data;
 //  }

 //  localStorage.setItem('pesanan', JSON.stringify(pesanan)); // ini dengan kondisi apapun tetap update pesanan


 // }else{
 //  alert('kondisi pesanan kosong ,,,, ndk tau apa eventnya');
 // }

 /*----------------------------*/
 let detail = JSON.parse(localStorage.getItem('pesanan_terfokus'));
 let pesanan = JSON.parse(localStorage.getItem('pesanan'));

 let jumlahPesananTerdeteksi = (pesanan.filter(p => p.kode_pemesanan === detail.kode_pemesanan)).length;
 let dateTime = datetimeEvent();
 detail.tanggal_simpan = dateTime.tanggal;
 detail.waktu_simpan = dateTime.waktu;
 detail.is_uploaded = 0;

 if(detail.pesanan.length > 0){
   if(jumlahPesananTerdeteksi === 0){
     pesanan.push(detail);
   }else if(jumlahPesananTerdeteksi > 0){
     let pesananIndex = pesanan.findIndex(p => p.kode_pemesanan === detail.kode_pemesanan);
     pesanan[pesananIndex] = detail;
   }

   localStorage.setItem('pesanan', JSON.stringify(pesanan));
   totalPembayaran = detail.total;
   $('#totalBayarPembayaran').html(detail.total);
   $('#totalItemPembayaran').html(detail.total_item);
   $('#modalpembayaran').modal('show');
   //// upload data ke server
   sinkronisasiPostDataPesanan(); 
 }else{
   alert('pesanan tidak ada');
 }
});

$('body').on('keyup','#nominalPembayaran', async function(){
 nominalPembayaran = await parseInt($(this).val());
 setKembalianPembayaran();
});

function setKembalianPembayaran(){
 let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));
 kembalianPembayaran = parseFloat(nominalPembayaran) - parseFloat(totalPembayaran);

 if(kembalianPembayaran < 0){
   $('.kembalianPembayaran').html('Tidak Mencukupi');
 }else{
   $('.kembalianPembayaran').html(kembalianPembayaran);
 }

 data.tunai = nominalPembayaran;
 data.kembalian = kembalianPembayaran;

 localStorage.setItem('pesanan_terfokus', JSON.stringify(data));
}


/******************* PROSES BAYAR **********************/
$('body').on('click','#btnBayarPembayaran', async function(){
 if(kembalianPembayaran >= 0){
   $('#modalpembayaran').modal('hide');
   let detail = JSON.parse(localStorage.getItem('pesanan_terfokus'));
   let pesanan = JSON.parse(localStorage.getItem('pesanan'));

   // var detail = getDetailPesanan(dataIndex);

   let dateTime = datetimeEvent();
   detail.status = 'sukses';
   detail.is_uploaded = 0;
   detail.tanggal_proses = dateTime.tanggal;
   detail.waktu_proses = dateTime.waktu;

   if(deleteDataPesanan(detail.kode_pemesanan))
     await addDataRiwayat(detail);

   await setHTMLPesananTersimpan();
   await setHTMLRiwayat();
   await sinkronisasiPostDataRiwayat();
 }else{
   alert('blm tau event apa yg bagus');
 }
});

function resetHTMLModalPembayaran(){
 $('#nominalPembayaran').val('');
 $('#kembalianPembayaran').html('');
}


async function setDefaultPesanan(){
 await resetStoragePesananTerfokus();
 $('#kode_pemesanan').html('Pesanan');
 $('#namaPelanggan').html('Pelanggan');
 $('#namaMeja').html('Tanpa Meja');

 await resetHTMLKalkulasiPesanan();
 await resetHTMLListPesanan();
 await resetHTMLModalPembayaran();
 return true;
}


/******************  SELESAI PEMBAYARAN ************************/
$('body').on('click','#btnPrintNotaSelesaiPembayaran', function(){
 alert('blom ada aksinya... masih nunggu si androidnya bang');
});

$('body').on('click','#btnKembaliSelesaiPembayaran', function(){
 resetHTMLModalSelesaiPembayaran();
});


function resetHTMLModalSelesaiPembayaran(){
 $('#kembalianSelesaiPembayaran').html('');
}
/*
|------------------------------------------------------------------------------------------------------
|                  PROSES HALAMAN PESANAN TERSIMPAN
|------------------------------------------------------------------------------------------------------
|
|  1. fungsi pencarian         => mencari melalui storage
|  2. fungsi setHTMLPesananTersimpan   => menampilkan data pesanan tersimpan dari storage
|                    => mengurutkan berdasarkan no_pemesanan secara ASC
|  3. fungsi setDetailPesanan      => menampilkan detail pesanan dengan pesanan_tersimpan_terfokus di storage
|  4. fungsi setBatalPesanan       => membatalkan pesanan secara langsung ke storage  
|                    => mengupdate data pesanan batal ke server
|                    => memindahkan data pesanan ke riwayat dengan status batal
|  5. fungsi setLanjutPesanan      => menampilkan detail data ke pesanan terfokus 
|  catatan : perlu dibuatkan sinkronisasi dengan alur
|      .ketika pertama kali klik menu akan load data dari server sebelum menampilkan dari menu. namum apaliba terjadi delay lebih dari 10 detik
|      .apabila lebih dari 10 detik maka dianggap gagal dan kembali load dari storage. atau apabila sedang offline maka langsung load dari storage
|      .sediakan storage khusus atau variable khusus untuk di set sebagai parameter koneksi
|      . apa bila gagal load data maka action sinkronisasi ulang akan diberikan notifikasi untuk dapat dikonfirmasi reload atau tidak.
|  
*/

function setHTMLPesananTersimpan(){
 let dataPesanan = JSON.parse(localStorage.getItem('pesanan'));

 let htmlList = "";
 $.each( dataPesanan , function( kp, p ) {
   htmlList = htmlList + '<li class="list-group-item px-2 py-2 opsi-list-pesanan-tersimpan active" data-index="'+p.kode_pemesanan+'">'+
            '<div class="d-flex bd-highlight">'+
              '<div class="flex-fill bd-highlight text-left" style="width: 50%">'+
                '<div>'+
                  '<p class="font-weight-bold mb-2">#'+p.kode_pemesanan+'</p>'+
                '</div>'+
                '<div>'+
                  '<p class="mb-0 font-small">'+p.tanggal_simpan+' '+ p.waktu_simpan +'</p>'+
                '</div>'+
              '</div>'+
              '<div class="flex-fill bd-highlight text-right" style="width: 50%">'+
                '<div>'+
                  '<p class="mb-0 font-small">'+p.nama_pelanggan+' '+p.nama_meja+'-'+p.nama_kategori_meja+'</p>'+
                '</div>'+
                '<div>'+
                  '<p class="font-weight-bold mb-2">'+ p.total_item +' item</p>'+
                '</div>'+
                '<div>'+
                  '<p class="font-weight-bold mb-2">Rp'+ p.total +'</p>'+
                '</div>'+
              '</div>'+
            '</div>'+
        '</li>';
 });

 $('#list-pesanan-tersimpan').html(htmlList);
}

function setDetailPesananTersimpan(data){
 $('#kodePesananTersimpan').html(data.kode_pemesanan);
 $('#tanggalWaktuPesananTersimpan').html(data.tanggal_simpan + ' ' + data.waktu_simpan);
 $('#namaPelangganPesananTersimpan').html(data.nama_pelanggan);
 $('#mejaPesananTersimpan').html( data.nama_meja +' - '+ data.nama_kategori_meja );

 $('#subtotalPesananTersimpan').html(data.subtotal);
 $('#totalDiskonPesananTersimpan').html(data.total_diskon);
 $('#totalPajakPesananTersimpan').html(data.total_pajak);
 $('#totalBiayaTambahanPesananTersimpan').html(data.total_biaya_tambahan);
 $('#totalPesananTersimpan').html(data.total);

 $('#btnLanjutPesananTersimpan').attr('data-index',data.kode_pemesanan);
 $('#btnModalHapusPesananTersimpan').attr('data-index',data.kode_pemesanan);

 let htmlMenu = "";
 $.each( data.pesanan , function( key, value ) {
   $.each( value.menu , function( k, v ) {
     htmlMenu = htmlMenu + '<li class="list-group-item p-3">'+
           '<div class="d-flex bd-highlight">'+
               '<div class="pr-1 flex-grow-1 bd-highlight text-left" style="width: 30%">'+
                 '<div class="text-truncate">'+
                   '<p class="mb-0 font-weight-bold">'+ v.nama_menu +'</p>'+
                   '<p class="mb-0"><small>Hot</small></p>'+
                   '<p class="mb-0"><small>Tambah Telur 2 Butir</small></p>'+
                 '</div>'+
               '</div>'+
               '<div class="bd-highlight px-1 text-center" style="width: 20%">Rp'+ v.harga +'&nbsp;X&nbsp;'+ v.jumlah +'</div>'+
               '<div class="bd-highlight text-right" style="width: 50%">'+
                 'Rp'+ v.total +
               '</div>'+
           '</div>'+
         '</li>';
     });
    });

    $('#list-menu-pesanan-tersimpan').html(htmlMenu);
}

$('body').on('click','.opsi-list-pesanan-tersimpan', function(){
 var dataIndex = $(this).attr('data-index');

 var dataPesanan = JSON.parse(localStorage.getItem('pesanan'));

 var data = dataPesanan.find( p => p.kode_pemesanan === dataIndex);

 setDetailPesananTersimpan(data);   
});


$('body').on('click','#btnLanjutPesananTersimpan', async function(){
 let dataIndex = await $(this).attr('data-index');

 let dataPesanan = await JSON.parse(localStorage.getItem('pesanan'));
 let data = await dataPesanan.find( p => p.kode_pemesanan === dataIndex);
 localStorage.setItem('pesanan_terfokus', JSON.stringify(data));
 await page('kasir');
});

async function setReOpenPesananTerfokus(){
 await resetHTMLKalkulasiPesanan();
 await resetHTMLListPesanan();
 await resetHTMLModalPembayaran();

 let pesanan = await JSON.parse(localStorage.getItem('pesanan_terfokus'));

 $('#kode_pemesanan').html('#' + pesanan.kode_pemesanan);
 if(pesanan.nama_pelanggan)
   $('#namaPelanggan').html(pesanan.nama_pelanggan);
 else
   $('#namaPelanggan').html("Pelanggan");

 if(pesanan.nama_meja)
   $('#namaMeja').html(pesanan.nama_meja);
 else
   $('#namaMeja').html("Tanpa Meja");


 await setListPesanan();
 await setKalkulasi();
}

$('body').on('click','#btnBatalPesananTersimpan', function(){
 let data = JSON.parse(localStorage.getItem('pesanan_terfokus'));

 if(data.pesanan.length > 0)
   $('#modalkonfirmasihapuspesanantersimpan').modal('show');
});

$('body').on('click','#btnModalHapusPesananTersimpan', async function(){
 /*
 | mengubah status data menjadi batal
 | 1. ambil data pesanan dan temukan index yg akan diubah 
 | 2. pesanan tersimpan terfokus akan dikirim ke storage riwayat
 | 3. data dari storage pesanan akan dihapus dengan menunjuk index terpilih
 | 5. update data pesanan
 | 6. reload data dari pesanan tersimpan
 | 7. sambil memperbaharui data riwayat dengan sinkronisasi riwayat
 */
 $('#modalkonfirmasihapuspesanantersimpan').modal('hide');

 var dataIndex = await $(this).attr('data-index');
 var detail = getDetailPesanan(dataIndex);

 let dateTime = datetimeEvent();
 detail.status = 'batal';
 detail.is_uploaded = 0;
 detail.tanggal_proses = dateTime.tanggal;
 detail.waktu_proses = dateTime.waktu;

 if(deleteDataPesanan(dataIndex))
   await addDataRiwayat(detail);

 await setHTMLPesananTersimpan();
 // await setHTMLRiwayat();
 await sinkronisasiPostDataRiwayat();
});

/*
|------------------------------------------------------------------------------------------------------
|                  PROSES HALAMAN RIWAYAT
|------------------------------------------------------------------------------------------------------
|
|  1. fungsi pencarian         => mencari melalui storage
|  2. fungsi setHTMLRiwayat      => menampilkan data riwayat dari storage
|                    => mengurutkan berdasarkan date terbaru secara DESC
|  3. fungsi setDetailRiwayat      => menampilkan detail pesanan dengan pesanan_tersimpan_terfokus di storage
|  4. fungsi sendEmailNota       => mengirim nota ke email melalui api backend  
|  5. fungsi printNotaRiwayat      => mencetak nota 
|  
*/

function setHTMLRiwayat(){
 let dataRiwayat = JSON.parse(localStorage.getItem('riwayat'));

 let tanggalArr = [];
 $.each( dataRiwayat, function( key, value ) {
   let jumlahSama = 0;
   $.each( tanggalArr, function( keyTanggal, v ) {
     if(v === value.tanggal_proses){
       jumlahSama = jumlahSama + 1;
     }
   });

   if(jumlahSama === 0)
     tanggalArr.push(value.tanggal_proses);
    });

 let htmlList = "";
 $.each( tanggalArr, function( keyTanggal, v ) {
   htmlList = htmlList + '<li class="list-group-item px-2 py-1" style="background: rgba(0, 142, 255, 0.26) !important">'+v+'</li>';
   $.each( dataRiwayat, function( key, value ) {
     if(value.tanggal_proses === v )
       htmlList = htmlList + '<li class="list-group-item opsi-list-riwayat px-2 py-2" data-index="'+value.kode_pemesanan+'">'+
                    '<div class="d-flex bd-highlight">'+
                      '<div class="flex-fill bd-highlight text-left" style="width: 50%">'+
                        '<div>'+
                          '<p class="font-weight-bold mb-2">#'+value.kode_pemesanan+'</p>'+
                        '</div>'+
                        '<div>'+
                          '<p class="mb-0 font-small">'+value.tanggal_proses+', '+value.waktu_proses+'</p>'+
                        '</div>'+
                      '</div>'+
                      '<div class="flex-fill bd-highlight text-right" style="width: 50%">'+
                        '<div>'+
                          '<p class="font-weight-bold mb-2">'+value.total+'</p>'+
                        '</div>'+
                        '<div>'+
                          '<p class="mb-0 font-small">'+value.nama_pelanggan+'</p>'+
                        '</div>'+
                      '</div>'+
                    '</div>'+
                '</li>';
   });
 });

 $('#list-riwayat').html(htmlList);
}

function setDetailRiwayat(data){
 $('#kodeRiwayat').html(data.kode_pemesanan);
 $('#tanggalWaktuRiwayat').html(data.tanggal_simpan + ' ' + data.waktu_simpan);
 $('#namaPelangganRiwayat').html(data.nama_pelanggan);
 $('#mejaRiwayat').html( data.nama_meja +' - '+ data.nama_kategori_meja );

 $('#subtotalRiwayat').html(data.subtotal);
 $('#totalDiskonRiwayat').html(data.total_diskon);
 $('#totalPajakRiwayat').html(data.total_pajak);
 $('#totalBiayaTambahanRiwayat').html(data.total_biaya_tambahan);
 $('#totalRiwayat').html(data.total);

 let htmlMenu = "";
 $.each( data.item , function( key, value ) {
   htmlMenu = htmlMenu + '<li class="list-group-item p-3">'+
         '<div class="d-flex bd-highlight">'+
             '<div class="pr-1 flex-grow-1 bd-highlight text-left" style="width: 30%">'+
               '<div class="text-truncate">'+
                 '<p class="mb-0 font-weight-bold">'+ value.nama_menu +'</p>'+
                 '<p class="mb-0"><small>Hot</small></p>'+
                 '<p class="mb-0"><small>Tambah Telur 2 Butir</small></p>'+
               '</div>'+
             '</div>'+
             '<div class="bd-highlight px-1 text-center" style="width: 20%">Rp'+ value.harga +'&nbsp;X&nbsp;'+ value.jumlah +'</div>'+
             '<div class="bd-highlight text-right" style="width: 50%">'+
               'Rp'+ value.total +
             '</div>'+
         '</div>'+
        '</li>';
    });

    $('#list-menu-riwayat').html(htmlMenu);
}

$('body').on('click','.opsi-list-riwayat', function(){
 var dataIndex = $(this).attr('data-index');
 var dataRiwayat = JSON.parse(localStorage.getItem('riwayat'));

 var data = dataRiwayat.find( p => p.kode_pemesanan === dataIndex);

 setDetailRiwayat(data);  
});


/*
|--------------------------------------------------------------------------
|                STORAGE PESANAN
|--------------------------------------------------------------------------
|  * storage pesanan menyimpan data yang telah diputuskan statusnya antara batal atau dibayar
|  * berikut daftar fungsi untuk mengelola data pesanan
|  1. getAllpesanan(kondisi status = false, ) => untuk mengambil seluruh pesanan dengan kondisi status jika ingin menggunakan seleksi dengan return data
|  2. getDetailpesanan(kode_pemesanan) => untuk mengambil detail pesanan , perlu mengirimkan parameter kode_pemesanan sebagai index dengan return data
|  3. addDatapesanan(data) => untuk menambah daftar pesanan diperlukan parameter data berupa format pesanan terfokus dengan return tru
|  4. updateDatapesanan(data,index) => untuk memperbaharui data pesanan, perlu mengirimkan data dan index yg berupa kode pemesanan dengan return tru
|  5. deleteDatapesanan(index) => untuk menghapus data pesanan, perlu mengirimkan index yg berupa kode pemesanan dengan return true
*/

function getAllPesanan(status = false){
 return JSON.parse(localStorage.getItem('pesanan'));
}

function getDetailPesanan(index){
 let pesanan = JSON.parse(localStorage.getItem('pesanan'));
 return pesanan.find(p=> p.kode_pemesanan === index);
}

async function addDataPesanan(data){
 let pesanan = await JSON.parse(localStorage.getItem('pesanan'));
 await pesanan.push(data);
 await localStorage.setItem('pesanan', JSON.stringify(pesanan));
 return true;
}

async function updateDataPesanan(data, index){
 let pesanan = await JSON.parse(localStorage.getItem('pesanan'));
 let pesananIndex = await pesanan.findIndex( p => p.kode_pemesanan === index);
 console.log(pesananIndex);
 pesanan[pesananIndex] = data;
 await localStorage.setItem('pesanan', JSON.stringify(pesanan));
 return true;
}
async function deleteDataPesanan(index){
 var pesanan = await JSON.parse(localStorage.getItem('pesanan'));
 var pesananIndex = await pesanan.findIndex( p => p.kode_pemesanan === index);
 console.log(pesananIndex);

 await pesanan.splice(pesananIndex,1);
 await localStorage.setItem('pesanan', JSON.stringify(pesanan));

 return true;
}



/*
|--------------------------------------------------------------------------
|                STORAGE RIWAYAT
|--------------------------------------------------------------------------
|  * storage riwayat menyimpan data yang telah diputuskan statusnya antara batal atau dibayar
|  * berikut daftar fungsi untuk mengelola data riwayat
|  1. getAllRiwayat(kondisi status = false, ) => untuk mengambil seluruh riwayat dengan kondisi status jika ingin menggunakan seleksi dengan return data
|  2. getDetailRiwayat(kode_pemesanan) => untuk mengambil detail riwayat , perlu mengirimkan parameter kode_pemesanan sebagai index dengan return data
|  3. addDataRiwayat(data) => untuk menambah daftar riwayat diperlukan parameter data berupa format pesanan terfokus dengan return tru
|  4. updateDataRiwayat(data,index) => untuk memperbaharui data riwayat, perlu mengirimkan data dan index yg berupa kode pemesanan dengan return tru
*/

function getAllRiwayat(status = false){
 if(status !== false){
   let riwayat = JSON.parse(localStorage.getItem('riwayat'));
   return riwayat.filter(p=> p.status === status);
 }else{
   return JSON.parse(localStorage.getItem('riwayat'));
 }
}

function getDetailRiwayat(index){
 let riwayat = JSON.parse(localStorage.getItem('riwayat'));
 return riwayat.find(p=> p.kode_pemesanan === index);
}

async function addDataRiwayat(data){
 let riwayat = await JSON.parse(localStorage.getItem('riwayat'));
 await riwayat.push(data);
 await localStorage.setItem('riwayat', JSON.stringify(riwayat));
 return true;
}

async function updateDataRiwayat(data, index){
 let riwayat = await JSON.parse(localStorage.getItem('riwayat'));
 let riwayatIndex = await riwayat.findIndex( p => p.kode_pemesanan === index);
 riwayat[riwayatIndex] = data;
 await localStorage.setItem('riwayat', JSON.stringify(riwayat));
 return true;
}




/*
|--------------------------------------------------------------------------
|          SINKRONISASI DATA PESANAN
|--------------------------------------------------------------------------
|
*/
function setDataPemesananStorageFormat(data){  
 let dataFormat = [];

 $.each( data, function( key, value ) {
   let detail = value;
   detail.pesanan = [];
     $.each( detail.item, function( k, v ) {
     if( (detail.pesanan.filter( p => p.id_tipe_penjualan === v.tipe_penjualan_id )).length <= 0){
       detail.pesanan.push({ id_tipe_penjualan: v.tipe_penjualan_id, nama_tipe_penjualan: v.nama_tipe_penjualan });
     }

     delete(v.bisnis_id);
     delete(v.created_at);
     delete(v.id_pemesanan_item);
     delete(v.kategori_menu_id);
     delete(v.nama_kategori_menu);
     delete(v.outlet_id);
     delete(v.pemesanan_id);
     delete(v.updated_at);
     delete(v.user_id);

     let pesananIndex = detail.pesanan.findIndex( p => p.id_tipe_penjualan === v.tipe_penjualan_id);
     detail.pesanan[pesananIndex].menu = [];
     detail.pesanan[pesananIndex].menu.push(v);
     });
   detail.is_uploaded = 1;
   delete(detail.created_at);
   delete(detail.updated_at);
   delete(detail.item);

   dataFormat.push(detail);

    });

 return dataFormat;
}
function sinkronisasiGetDataPesanan(){    //    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx
 $.ajax({
   url: 'https://api-backoffice.kapcake.com/api/kasir/pemesanan',
   headers: {
           // 'Content-Type':'application/json',
           'Accept':'application/json',
           'Authorization':'Bearer '+getToken(),
          },
          method: 'GET',
          dataType: 'json',
          data: {
           outlet_id : outlet_terpilih_id,
          },
          success: function(data){
           console.log(data);
     // let pesanan = JSON.parse(localStorage.getItem('pesanan'));
   }
 });
}
/// post data pemesanan
async function setDataPemesananAPIFormat(){
 let dataFormat = await JSON.parse(localStorage.getItem('pesanan'));
 let dataSetReturn = [];
 for (var i = 0; i < dataFormat.length; i++) {
   if(dataFormat[i]['is_uploaded'] === 0){

     delete(dataFormat[i]['is_uploaded']);

     if(dataFormat[i]['tanggal_proses'] === false){ // diberikan filter agar tidak terupload
       dataFormat[i]['tanggal_proses'] = null;
       dataFormat[i]['waktu_proses'] = null;
     }



     dataFormat[i]['item'] = [];
     $.each( dataFormat[i]['pesanan'], function( k, v ) {
       $.each( v.menu, function( kc, vc ) {
         dataFormat[i]['item'].push(vc);
       });
     });

     delete(dataFormat[i]['pesanan']);

     dataSetReturn.push(dataFormat[i]);
   }

 }
 return dataSetReturn;
}

async function sinkronisasiPostDataPesanan(){
 let data = await JSON.parse(localStorage.getItem('data'));
 let dataPemesanan = await setDataPemesananAPIFormat();
 $.ajax({
   url: 'https://api-backoffice.kapcake.com/api/kasir/pemesanan',
   headers: {
         // 'Accept':'application/json', 
         'Authorization':'Bearer '+getToken(),
     },
     method: 'POST',
     data: {
       outlet_id : data.outlet_terpilih_id,
       pemesanan : dataPemesanan
     },
     success: async function(res){

       // set storage pesanan di taruh di success karena harus cek apakah data terupload atau tidak gitueee
     let pesanan = JSON.parse(localStorage.getItem('pesanan'));
     await $.each( pesanan, function( key, value ) {
       pesanan[key].is_uploaded = 1;
     });
     await localStorage.setItem('pesanan', JSON.stringify(pesanan)); 

     },
     error: function(res){
       alert('connection lost');
     }
 });
}



/*
|--------------------------------------------------------------------------
|          SINKRONISASI DATA RIWAYAT
|--------------------------------------------------------------------------
|
*/

async function setDataRiwayatAPIFormat(){
 let dataFormat = await JSON.parse(localStorage.getItem('riwayat'));
 let dataSetReturn = [];
 for (var i = 0; i < dataFormat.length; i++) {
   if(dataFormat[i]['is_uploaded'] === 0){

     delete(dataFormat[i]['is_uploaded']);

     dataFormat[i]['item'] = [];
     $.each( dataFormat[i]['pesanan'], function( k, v ) {
       $.each( v.menu, function( kc, vc ) {
         dataFormat[i]['item'].push(vc);
       });
     });

     delete(dataFormat[i]['pesanan']);

     dataSetReturn.push(dataFormat[i]);
   }

 }
 return dataSetReturn;
}

async function sinkronisasiPostDataRiwayat(){
 let data = await JSON.parse(localStorage.getItem('data'));
 let dataRiwayat = await setDataRiwayatAPIFormat();
 $.ajax({
   url: 'https://api-backoffice.kapcake.com/api/kasir/penjualan',
   headers: {
         // 'Accept':'application/json', 
         'Authorization':'Bearer '+getToken(),
     },
     method: 'POST',
     data: {
       outlet_id : data.outlet_terpilih_id,
       penjualan : dataRiwayat
     },
     success: async function(res){
       // set storage pesanan di taruh di success karena harus cek apakah data terupload atau tidak gitueee
     let riwayat = JSON.parse(localStorage.getItem('riwayat'));
     await $.each( riwayat, function( key, value ) {
       riwayat[key].is_uploaded = 1;
     });
     await localStorage.setItem('riwayat', JSON.stringify(riwayat)); 
     },
     error: function(res){
       alert('connection lost');
     }
 });
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
           ///////////////////     FUNGSI TAMBAHAN    ////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function datetimeEvent(){
 var d = new Date();
 return {
   'tanggal' : d.getFullYear() + "-" + (d.getMonth()+1) + "-" + d.getDate(),
   'waktu' : d.getHours() + ":" + d.getMinutes()
 }
}