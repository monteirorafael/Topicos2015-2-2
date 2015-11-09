package br.grupointegrado.ads.flappyBird;


import android.graphics.Color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.lang.reflect.Array;

/**
 * Created by Rafael on 28/09/2015.
 */
public class TelaJogo extends TelaBase {


    private OrthographicCamera camera; //camera do jogo
    private World mundo; // representa o mundo do Box2D
   private Body chao; // corpo do chão
   private  Passaro passaro;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();

    private int pontuacao = 0;
    private BitmapFont fontePontuacao;
    private Stage palcoInformacoes;
    private Label lbPontuacao;
    private ImageButton btnPlay;
    private ImageButton btnGameOver;
    private OrtographicCamera cameraInfo;
    private boolean gameOver = false;

    private Box2DDebugRenderer debug; //desenha o mundo na tela para ajudar no desenvolvimento.

    public TelaJogo(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth()/Util.ESCALA, Gdx.graphics.getHeight()/Util.ESCALA);
        debug = new Box2DDebugRenderer();
        cameraInfo = new OrtographicCamera(Gdx.graphics.getWidth(), Gdx.graphica.getHeight());
        mundo = new World(new Vector2(0,-9.8f), false);
        //IMPLEMENTAR MUNDO.SETCONTACTLISTENER();
        
	   initChao();
        initPassaro();
        initFontes();
        initInformacaoes();
    }

    public void detectarColisoes(Fixture fixtureA, Fixture fixtureB){
        if ("PASSARO".equals(fixtureA.getUserData()) ||
                "PASSARO".equals(fixtureB.getUserData())){
            //game over
            gameOver = true;
        }
    }

private void initChao() {
     chao = Util.criarCorpo(mundo, 			BodyDef.BodyType.StaticBody,0,0);
    }

    private void initInformacaoes() {
        palcoInformacoes = new Stage(new FillViewport(cameraInfo.viewportWidth,
                cameraInfo.vierportHeight, cameraInfo));

        Gdx.input.setInputPrecessor(palcoInformacoes);

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fontePontuacao;

        lbPontuacao = new Label("0", estilo);
        palcoInformacoes.addActor(lbPontuacao);

    }
    private void initFontes()() {
       FreeTypeFontGenerator.FreeTypeFontParameter fonteParam =
               new FreeTypeFontGenerator.FreeTypeFontParameter();
        fonteParam.size = 56;
        fonteParam.color = Color.WHITE;
        fonteParam.shadowColor = Color.BLACK;
        fonteParam.shadowOffSetX = 4;
        fonteParam.shadowOffSetY = 4;

        FreeTypeFontGenerator gerador =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

        fontePontuacao = gerador.generateFont(fonteParam);
        gerador.dispose();
    }



 private void initPassaro() {
 
        passaro = new Passaro(mundo,camera,null);
 
     }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1); //Limpa tela e pinta cor de fundo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // mantem o buffer de cores

        capturaTeclas();
        atualizar(delta);
        renderizar(delta);


        debug.render(mundo, camera.combined.cpy().scl(Util.PIXEL_METRO));
    }

    private boolean pulando = false;
    private void capturaTeclas() {
        pulando = false;
        if (Gdx.input.justTouched()){
            pulando = true;
        }

    }


private void atualizar(float delta) {

        palcoInformacoes.act(delta);
        passaro.atualizar(delta);
        mundo.step(1f / 60f, 6, 2);

        atualizarInformacoes();
        atualizarObstaculos();
        atualizarCamera();
        atualizarChao();
    if(pulando){
        passaro.pular();
    }
}

    private void atualizarInformacoes() {
        lbPontuacao.setText(pontuacao + "");
        lbPontuacao.setPosition(cameraInfo.viewportWidth / 2 - lbPontuacao.getPrefWidth() /2,
                cameraInfo.viewportHeight - lbPontuacao.getPrefHeight());
        }

    private void atualizarObstaculos() {
        //enquanto a lista tiver menos do que 4, crie obstaculos
        while (obstaculos.size < 4){
            Obstaculo ultimo = null;
            if(obstaculos.size >0){
                ultimo = obstaculos.peek()
            }
            Obstaculo o = new Obstaculo(mundo,camera,ultimo);
            obstaculos.add(o);
        }
        //verifica se os obstaculos sairam da tela para remove-los

        for (Obstaculo o : obstaculos){
            float inicioCamera = passaro.getCorpo().getPosition().x -
                    (camera.viewportWidth / 2 / Util.PIXEL_METRO) - o.getLargura();
            if (inicioCamera > o.getPosX()){
                o.remover();
                obstaculos.removeValue(o,true);
            }else if(!o.isPassou() && o.getPosX() < passaro.getCorpo().getPosition().x){
                o.setPassou(true);
                pontuacao++;
                //reproduzir som
            }

        }

    }



    private void atualizarCamera() {
        camera.position.x = (passaro.getCorpo().getPosition().x - 32 / Util.PIXEL_METRO )* Util.PIXEL_METRO;
        camera.update;
    }

/**
     * Atualiza a posição do chão para acompanhar o pássaro
     */
    private void atualizarChao() {
        Vector2 posicao = passaro.getCorpo().getPosition();

        chao.setTransform(posicao.x, 0,0);

    }

/**
     * Renderizar/desenhar as imagens
     * @param delta
     */
    private void renderizar(float delta) {

        palcoInformacoes.draw();

     }




    @Override
    public void resize(int width, int height) {
camera.setToOrtho(false, width / Util.ESCALA, height / Util.ESCALA);
        camera.update();
       redimensionaChao();
        cameraInfo.setToOrtho(false, width, height);
        cameraInfo.update();
    }


 private void redimensionaChao() {
        chao.getFixtureList().clear();
        float largura = camera.viewportWidth / Util.PIXEL_METRO;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, Util.ALTURA_CHAO / 2);
        Fixture forma = Util.criarForma(chao,shape,"CHAO");
        shape.dispose();
     }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
	debug.dispose();
     mundo.dispose();
     palcoInformacoes.dispose();
        fontePontuacao.dispose();

    }
}
